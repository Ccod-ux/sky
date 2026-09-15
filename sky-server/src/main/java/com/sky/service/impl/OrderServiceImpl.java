package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.entity.User;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端订单业务实现。
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    /** 课程项目统一使用一分钱测试支付，避免误扣真实订单金额。 */
    private static final BigDecimal DEMO_PAYMENT_AMOUNT = new BigDecimal("0.01");

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        return new PageResult(page.getTotal(), toOrderVOList(page.getResult()));
    }

    /**
     * 取消订单
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO)  {
        Long userId = BaseContext.getCurrentId();
        orderMapper.cancelById(ordersCancelDTO.getId());
    }

    /**
     * 为分页结果补充“菜品名称*数量”字符串。
     */
    private List<OrderVO> toOrderVOList(List<Orders> ordersList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(ordersList)) {
            return orderVOList;
        }

        for (Orders orders : ordersList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            orderVO.setOrderDishes(getOrderDishes(orders.getId()));
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    private String getOrderDishes(Long orderId) {
        List<OrderDetail> details = orderDetailMapper.getByOrderId(orderId);
        if (CollectionUtils.isEmpty(details)) {
            return "";
        }
        return details.stream()
                .map(detail -> detail.getName() + "*" + detail.getNumber() + ";")
                .collect(Collectors.joining());
    }

    /**
     * 各状态订单数量统计
     */
    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO result = new OrderStatisticsVO();
        result.setToBeConfirmed(orderMapper.countByStatus(Orders.TO_BE_CONFIRMED));
        result.setConfirmed(orderMapper.countByStatus(Orders.CONFIRMED));
        result.setDeliveryInProgress(orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS));
        return result;
    }

    /**
     * 查询订单详情
     */
    @Override
    public OrderVO details(Long id) {
        Orders orders = getRequiredOrder(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        orderVO.setOrderDishes(getOrderDishes(id));
        return orderVO;
    }

    /**
     * 接单
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = getRequiredOrder(ordersConfirmDTO.getId());
        requireStatus(orders, Orders.TO_BE_CONFIRMED);

        orderMapper.update(Orders.builder()
                .id(orders.getId())
                .status(Orders.CONFIRMED)
                .build());
    }

    /**
     * 拒单
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        Orders orders = getRequiredOrder(ordersRejectionDTO.getId());
        requireStatus(orders, Orders.TO_BE_CONFIRMED);
        refundIfPaid(orders);

        orderMapper.update(Orders.builder()
                .id(orders.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build());
    }

    /**
     * 开始配送
     */
    @Override
    public void delivery(Long id) {
        Orders orders = getRequiredOrder(id);
        requireStatus(orders, Orders.CONFIRMED);

        orderMapper.update(Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build());
    }

    /**
     * 完成订单
     */
    @Override
    public void complete(Long id) {
        Orders orders = getRequiredOrder(id);
        requireStatus(orders, Orders.DELIVERY_IN_PROGRESS);

        orderMapper.update(Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build());
    }

    /**
     * 用户取消订单
     */
    @Override
    @Transactional
    public void cancel(Long id) throws Exception {
        Long userId = BaseContext.getCurrentId();
        Orders orders = getRequiredOrder(id);

        if (!userId.equals(orders.getUserId())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if (!Orders.PENDING_PAYMENT.equals(orders.getStatus())
                && !Orders.TO_BE_CONFIRMED.equals(orders.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders updateOrder = Orders.builder()
                .id(id)
                .status(Orders.CANCELLED)
                .cancelReason("用户取消")
                .cancelTime(LocalDateTime.now())
                .build();

        if (Orders.PAID.equals(orders.getPayStatus())) {
            refundIfPaid(orders);
            updateOrder.setPayStatus(Orders.REFUND);
        }

        orderMapper.update(updateOrder);
        log.info("用户取消订单成功：orderId={}, userId={}", id, userId);
    }

    /**
     * 查询当前用户的订单详情
     */
    @Override
    public OrderVO userDetails(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = getRequiredOrder(id);

        if (!userId.equals(orders.getUserId())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        orderVO.setOrderDishes(orderDetailList.stream()
                .map(detail -> detail.getName() + "*" + detail.getNumber() + ";")
                .collect(Collectors.joining()));
        return orderVO;
    }

    /**
     * 分页查询当前用户的历史订单
     */
    @Override
    public PageResult historyOrders(Integer page, Integer pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);

        OrdersPageQueryDTO queryDTO = new OrdersPageQueryDTO();
        queryDTO.setUserId(BaseContext.getCurrentId());
        queryDTO.setStatus(status);

        Page<Orders> ordersPage = orderMapper.pageQuery(queryDTO);
        List<OrderVO> records = new ArrayList<>();

        for (Orders orders : ordersPage.getResult()) {
            List<OrderDetail> detailList = orderDetailMapper.getByOrderId(orders.getId());
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            orderVO.setOrderDetailList(detailList);
            orderVO.setOrderDishes(detailList.stream()
                    .map(detail -> detail.getName() + "*" + detail.getNumber() + ";")
                    .collect(Collectors.joining()));
            records.add(orderVO);
        }

        return new PageResult(ordersPage.getTotal(), records);
    }

    /**
     * 用户催单
     */
    @Override
    public void reminder(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = getRequiredOrder(id);

        if (!userId.equals(orders.getUserId())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Map<String, Object> message = new HashMap<>();
        message.put("type", 2);
        message.put("orderId", id);
        message.put("content", "订单号：" + orders.getNumber());

        webSocketServer.sendToAllClient(JSON.toJSONString(message));
        log.info("用户催单消息已发送：orderId={}, userId={}", id, userId);
    }

    /**
     * 用户发起微信支付
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        if (ordersPaymentDTO == null
                || !StringUtils.hasText(ordersPaymentDTO.getOrderNumber())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if (!Integer.valueOf(1).equals(ordersPaymentDTO.getPayMethod())) {
            throw new OrderBusinessException(MessageConstant.PAYMENT_METHOD_NOT_SUPPORTED);
        }

        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());
        if (orders == null || !userId.equals(orders.getUserId())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if (!ordersPaymentDTO.getPayMethod().equals(orders.getPayMethod())) {
            throw new OrderBusinessException(MessageConstant.PAYMENT_METHOD_NOT_SUPPORTED);
        }

        if (!Orders.PENDING_PAYMENT.equals(orders.getStatus())
                || !Orders.UN_PAID.equals(orders.getPayStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        User user = userMapper.getById(userId);
        if (user == null || !StringUtils.hasText(user.getOpenid())) {
            throw new OrderBusinessException(MessageConstant.PAYMENT_FAILED);
        }

        try {
            JSONObject paymentData = weChatPayUtil.pay(
                    orders.getNumber(),
                    DEMO_PAYMENT_AMOUNT,
                    "苍穹外卖订单",
                    user.getOpenid());

            if (!StringUtils.hasText(paymentData.getString("paySign"))) {
                log.error("微信预支付失败：orderNumber={}, response={}",
                        orders.getNumber(), paymentData);
                throw new OrderBusinessException(MessageConstant.PAYMENT_FAILED);
            }

            return OrderPaymentVO.builder()
                    .nonceStr(paymentData.getString("nonceStr"))
                    .paySign(paymentData.getString("paySign"))
                    .timeStamp(paymentData.getString("timeStamp"))
                    .signType(paymentData.getString("signType"))
                    .packageStr(paymentData.getString("package"))
                    .build();
        } catch (OrderBusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("调用微信支付失败：orderNumber={}", orders.getNumber(), ex);
            throw new OrderBusinessException(MessageConstant.PAYMENT_FAILED);
        }
    }


    /**
     * 用户下单
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();

        AddressBook addressBook = addressBookMapper.getByIdAndUserId(
                ordersSubmitDTO.getAddressBookId(), userId);
        if (addressBook == null) {
            throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (CollectionUtils.isEmpty(shoppingCartList)) {
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getProvinceName()
                + addressBook.getCityName()
                + addressBook.getDistrictName()
                + addressBook.getDetail());

        orderMapper.insert(orders);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        shoppingCartMapper.deleteByUserId(userId);

        log.info("用户下单成功：orderId={}, orderNumber={}, userId={}",
                orders.getId(), orders.getNumber(), userId);

        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    /**
     * 再来一单
     */
    @Override
    @Transactional
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();

        Orders orders = getRequiredOrder(id);
        if (!userId.equals(orders.getUserId())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        List<OrderDetail> detailList = orderDetailMapper.getByOrderId(id);
        for (OrderDetail detail : detailList) {
            ShoppingCart cart = new ShoppingCart();
            BeanUtils.copyProperties(detail, cart, "id");
            cart.setUserId(userId);
            cart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(cart);
        }
    }

    /**
     * 根据订单id查询订单
     */
    private Orders getRequiredOrder(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        return orders;
    }

    /**
     * 校验订单状态是否符合要求
     */
    private void requireStatus(Orders orders, Integer requiredStatus) {
        if (!requiredStatus.equals(orders.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    /**
     * 课程项目使用 0.01 元测试退款；正式环境应改成真实退款金额。
     */
    private void refundIfPaid(Orders orders) throws Exception {
        if (Orders.PAID.equals(orders.getPayStatus())) {
            String refundResult = weChatPayUtil.refund(
                    orders.getNumber(),
                    orders.getNumber(),
                    new BigDecimal("0.01"),
                    new BigDecimal("0.01"));
            log.info("申请退款：{}", refundResult);
        }
    }
}

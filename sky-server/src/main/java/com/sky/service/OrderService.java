package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/**
 * 订单业务层。
 */
public interface OrderService {

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    void repetition(Long id);

    OrderStatisticsVO statistics();

    OrderVO details(Long id);

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    void delivery(Long id);

    void complete(Long id);

    void cancel(Long id) throws Exception;

    OrderVO userDetails(Long id);

    PageResult historyOrders(Integer page, Integer pageSize, Integer status);

    void reminder(Long id);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);
}

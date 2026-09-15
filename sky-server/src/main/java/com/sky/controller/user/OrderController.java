package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端订单接口。
 */
@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO 订单信息
     * @return 订单信息
     */
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单：addressBookId={}, amount={}"
                , ordersSubmitDTO.getAddressBookId(), ordersSubmitDTO.getAmount());
        return Result.success(orderService.submit(ordersSubmitDTO));
    }

    /**
     * 再来一单
     *
     * @param id 订单id
     */
    @PostMapping("/repetition/{id}")
    public Result<String> repetition(@PathVariable("id") Long id) {
        log.info("再来一单：orderId={}", id);
        orderService.repetition(id);
        return Result.success();
    }

    /**
     * 取消订单
     * @param id 订单id
     */
    @PutMapping("/cancel/{id}")
    public Result<String> cancel(@PathVariable("id") Long id) throws Exception {
        log.info("取消订单：orderId={}", id);
        orderService.cancel(id);
        return Result.success();
    }

    /**
     * 查询当前用户的订单详情
     * @param id 订单id
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> orderDetail(@PathVariable("id") Long id) {
        log.info("用户查询订单详情：orderId={}", id);
        return Result.success(orderService.userDetails(id));
    }

    /**
     * 分页查询当前用户的历史订单
     */
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(
            @RequestParam("page") Integer page,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status) {
        log.info("用户查询历史订单：page={}, pageSize={}, status={}",
                page, pageSize, status);
        return Result.success(orderService.historyOrders(page, pageSize, status));
    }

    /**
     * 催单
     */
    @GetMapping("/reminder/{id}")
    public Result<String> reminder(@PathVariable("id") Long id) {
        log.info("催单：orderId={}", id);
        orderService.reminder(id);
        return Result.success();
    }

    /**
     * 订单支付
     */
    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("订单支付：{}", ordersPaymentDTO);
        return Result.success(orderService.payment(ordersPaymentDTO));
    }
}

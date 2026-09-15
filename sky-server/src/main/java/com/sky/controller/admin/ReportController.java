package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * 数据统计相关接口。
 */
@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "数据统计相关接口")
public class ReportController {
    @Autowired
    private ReportService reportService;
    /**
     * 订单统计
     */
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> countOrder(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单数据统计：{},{}", begin, end);
        OrderReportVO orderReportVO = reportService.getOrderStatistics(begin, end);
        return Result.success(orderReportVO);
    }

    /**
     * 营业额统计接口
     */
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> countTurnover(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额数据统计：{},{}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }

    /**
     * 用户统计接口
     */
    @GetMapping("/userStatistics")
    public Result<UserReportVO> countUser(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计: {}, {}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    /**
     * 销量排名前十
     */
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销量排名Top10：{},{}", begin, end);
        return Result.success(reportService.getSalesTop10(begin, end));
    }

    /**
     * 导出最近三十天运营数据报表
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        log.info("导出运营数据报表");
        reportService.exportBusinessData(response);
    }
}

package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据统计业务实现类。
 */
@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 订单统计
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
        }

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = date.atStartOfDay();
            LocalDateTime endTime = date.plusDays(1).atStartOfDay();

            Integer orderCount = getOrderCount(beginTime, endTime, null);
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        int totalOrderCount = orderCountList.stream().mapToInt(Integer::intValue).sum();
        int validOrderCount = validOrderCountList.stream().mapToInt(Integer::intValue).sum();
        double orderCompletionRate = totalOrderCount == 0
                ? 0.0
                : (double) validOrderCount / totalOrderCount;

        return OrderReportVO.builder()
                .dateList(join(dateList))
                .orderCountList(join(orderCountList))
                .validOrderCountList(join(validOrderCountList))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 营业额统计
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);

            Map<String, Object> map = new HashMap<>();
            map.put("begin", date.atStartOfDay());
            map.put("end", date.plusDays(1).atStartOfDay());
            map.put("status", Orders.COMPLETED);

            Double turnover = orderMapper.sumByMap(map);
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(join(dateList))
                .turnoverList(join(turnoverList))
                .build();
    }

    /**
     * 用户统计
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);

            Map<String, Object> map = new HashMap<>();
            map.put("end", date.plusDays(1).atStartOfDay());

            Integer totalUserCount = userMapper.countByMap(map);

            map.put("begin", date.atStartOfDay());
            Integer newUserCount = userMapper.countByMap(map);

            newUserList.add(newUserCount);
            totalUserList.add(totalUserCount);
        }
        return UserReportVO.builder()
                .dateList(join(dateList))
                .newUserList(join(newUserList))
                .totalUserList(join(totalUserList))
                .build();
    }

    /**
     * 销量排名前十
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = begin.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        List<String> nameList = salesTop10.stream()
                .map(GoodsSalesDTO::getName)
                .collect(Collectors.toList());
        List<Integer> numberList = salesTop10.stream()
                .map(GoodsSalesDTO::getNumber)
                .collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(join(nameList))
                .numberList(join(numberList))
                .build();
    }

    /**
     * 导出最近三十天运营数据报表
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        BusinessDataVO overviewData = workspaceService.getBusinessData(
                dateBegin.atStartOfDay(),
                dateEnd.atTime(LocalTime.MAX));

        InputStream template = getClass().getClassLoader()
                .getResourceAsStream("template/运营数据报表模板.xlsx");
        if (template == null) {
            throw new IllegalStateException("未找到运营数据报表模板");
        }

        try (InputStream inputStream = template;
             XSSFWorkbook excel = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = excel.getSheet("Sheet1");

            // 填充统计时间和概览数据。
            sheet.getRow(1).getCell(1)
                    .setCellValue("时间：" + dateBegin + "至" + dateEnd);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(overviewData.getTurnover());
            row.getCell(4).setCellValue(overviewData.getOrderCompletionRate());
            row.getCell(6).setCellValue(overviewData.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(overviewData.getValidOrderCount());
            row.getCell(4).setCellValue(overviewData.getUnitPrice());

            // Excel 第8至37行对应最近三十天的每日明细。
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                BusinessDataVO dailyData = workspaceService.getBusinessData(
                        date.atStartOfDay(),
                        date.atTime(LocalTime.MAX));

                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(dailyData.getTurnover());
                row.getCell(3).setCellValue(dailyData.getValidOrderCount());
                row.getCell(4).setCellValue(dailyData.getOrderCompletionRate());
                row.getCell(5).setCellValue(dailyData.getUnitPrice());
                row.getCell(6).setCellValue(dailyData.getNewUsers());
            }

            String fileName = URLEncoder.encode("运营数据报表.xlsx", "UTF-8")
                    .replace("+", "%20");
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename*=UTF-8''" + fileName);

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                excel.write(outputStream);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("导出运营数据报表失败", e);
        }
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }

    private String join(List<?> values) {
        return values.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }


}

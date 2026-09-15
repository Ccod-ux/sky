package com.sky.service.impl;

import com.sky.mapper.WorkspaceMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工作台业务实现类
 */
@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private WorkspaceMapper workspaceMapper;

    /**
     * 查询今日运营数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        BusinessDataVO businessDataVO = workspaceMapper.getBusinessData(begin, end);
        return businessDataVO;
    }

    /**
     * 查询今日订单管理数据。
     */
    @Override
    public OrderOverViewVO getOrderOverView() {
        LocalDateTime begin = LocalDate.now().atStartOfDay();
        LocalDateTime end = begin.plusDays(1);
        return workspaceMapper.getOrderOverView(begin, end);
    }

    /**
     * 查询菜品总览。
     */
    @Override
    public DishOverViewVO getDishOverView() {
        return workspaceMapper.getDishOverView();
    }

    /**
     * 查询套餐总览。
     */
    @Override
    public SetmealOverViewVO getSetmealOverView() {
        return workspaceMapper.getSetmealOverView();
    }
}

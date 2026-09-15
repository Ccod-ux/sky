package com.sky.mapper;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 工作台数据访问层。
 */
@Mapper
public interface WorkspaceMapper {
    /**
     * 查询今日运营数据
     * @return
     */
    BusinessDataVO getBusinessData(@Param("begin") LocalDateTime begin,
                                   @Param("end") LocalDateTime end);

    /** 查询今日订单管理数据。 */
    OrderOverViewVO getOrderOverView(@Param("begin") LocalDateTime begin,
                                     @Param("end") LocalDateTime end);

    /** 查询菜品总览。 */
    DishOverViewVO getDishOverView();

    /** 查询套餐总览。 */
    SetmealOverViewVO getSetmealOverView();
}

package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问层。
 */
@Mapper
public interface OrderMapper {

    /** 新增订单 */
    void insert(Orders orders);

    /**
     * 分页查询订单
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据ID查询订单
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 根据订单号查询订单
     */
    @Select("select * from orders where number = #{number}")
    Orders getByNumber(String number);

    /**
     * 根据状态查询订单数量
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countByStatus(Integer status);

    /**
     * 根据条件统计订单数量
     */
    Integer countByMap(Map<String, Object> map);

    /**
     * 根据条件统计营业额
     */
    Double sumByMap(Map<String, Object> map);

    /**
     * 更新订单
     */
    void update(Orders orders);

    /**
     * 查询销售量Top10商品
     */
    List<GoodsSalesDTO> getSalesTop10(
            @Param("begin") LocalDateTime begin,
            @Param("end") LocalDateTime end);
    /**
     * 取消订单
     */
    @Update("update orders set status = 6 where id = #{id}")
    void cancelById(Long id);
}

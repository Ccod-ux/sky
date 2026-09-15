package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 套餐与菜品关联关系的数据访问层。
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品 id 集合查询关联的套餐 id。
     */
    List<Long> getSetmealIdsByDishIds(@Param("dishIds") List<Long> dishIds);

    /**
     * 批量新增套餐菜品关联关系。
     */
    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    /** 根据套餐 id 删除关联关系 */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}

package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /** 批量新增菜品口味 */
    void insertBatch(List<DishFlavor> flavors);

    /** 根据菜品id删除口味 */
    void deleteByDishId(Long dishId);

    /** 根据菜品id查询口味 */
    List<DishFlavor> getByDishId(Long dishId);
}

package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品及口味
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品及口味
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 修改菜品及口味
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 菜品起售、停售
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据分类id查询菜品
     */
    List<Dish> list(Long categoryId);

    /**
     * 根据条件查询菜品，并为每个菜品组装口味数据
     */
    List<DishVO> listWithFlavor(Dish dish);
}

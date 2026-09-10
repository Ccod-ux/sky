package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品及口味
     */
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        throw new UnsupportedOperationException("TODO: 实现新增菜品及口味");
    }

    /**
     * 菜品分页查询
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        throw new UnsupportedOperationException("TODO: 实现菜品分页查询");
    }

    /**
     * 批量删除菜品
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        throw new UnsupportedOperationException("TODO: 实现批量删除菜品");
    }

    /**
     * 根据id查询菜品及口味
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        throw new UnsupportedOperationException("TODO: 实现根据id查询菜品及口味");
    }

    /**
     * 修改菜品及口味
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        throw new UnsupportedOperationException("TODO: 实现修改菜品及口味");
    }

    /**
     * 菜品起售、停售
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        throw new UnsupportedOperationException("TODO: 实现菜品起售、停售");
    }

    /**
     * 根据分类id查询菜品
     */
    @Override
    public List<Dish> list(Long categoryId) {
        throw new UnsupportedOperationException("TODO: 实现根据分类id查询菜品");
    }
}


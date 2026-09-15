package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * 套餐业务层
 */
public interface SetmealService {
    /**
     * 新增套餐
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /** 套餐分页查询 */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /** 批量删除套餐 */
    void deleteBatch(List<Long> ids);

    /** 根据 id 查询套餐及其菜品 */
    SetmealVO getByIdWithDish(Long id);

    /** 修改套餐 */
    void update(SetmealDTO setmealDTO);

    /** 套餐起售、停售 */
    void startOrStop(Integer status, Long id);

    /** 根据条件查询套餐 */
    List<Setmeal> list(Setmeal setmeal);

    /** 根据套餐 id 查询套餐内的菜品 */
    List<DishItemVO> getDishItemById(Long id);
}

package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMapper {

    /** 新增菜品 */
    void insert(Dish dish);

    /** 菜品分页查询 */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /** 根据id查询菜品 */
    Dish getById(Long id);

    /** 根据id删除菜品 */
    void deleteById(Long id);

    /** 动态修改菜品 */
    void update(Dish dish);

    /** 动态条件查询菜品 */
    List<Dish> list(Dish dish);

    /** 根据分类id统计菜品数量，供删除分类前校验使用 */
    Integer countByCategoryId(Long categoryId);
}

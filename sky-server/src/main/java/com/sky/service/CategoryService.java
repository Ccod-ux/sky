package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 新增分类
     * @param categoryDTO 分类DTO
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 启用、禁用分类
     * @param status 分类状态，1为启用，0为禁用
     * @param id 分类id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据类型查询分类
     * @param type 分类类型，1为菜品分类，2为套餐分类
     * @return 分类列表
     */
    List<Category> list(Integer type);

    /**
     * 根据id删除分类
     * @param id
     */
    void deleteById(Long id);

    /**
     * 分类分页查询
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 修改分类
     */
    void update(CategoryDTO categoryDTO);
}

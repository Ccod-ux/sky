package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增分类
     * @param categoryDTO 分类DTO
     */
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
        categoryMapper.insert(category);
    }

    /**
     * 启用、禁用分类
     * @param status 分类状态，1为启用，0为禁用
     * @param id 分类id
     */
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.updateStatus(category);
    }

    /**
     * 根据类型查询分类
     * @param type 分类类型，1为菜品分类，2为套餐分类
     * @return 分类列表
     */
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
    }
}

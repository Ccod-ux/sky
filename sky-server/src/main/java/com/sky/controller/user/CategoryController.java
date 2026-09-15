package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端分类接口
 */
@RestController("userCategoryController")
@RequestMapping("/user/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据类型查询启用中的分类；type 不传时查询全部启用分类
     */
    @GetMapping("/list")
    public Result<List<Category>> list(
            @RequestParam(value = "type", required = false) Integer type) {
        log.info("用户端查询分类：type={}", type);
        return Result.success(categoryService.list(type));
    }
}

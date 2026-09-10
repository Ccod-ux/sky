package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品信息：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        return Result.success(dishService.pageQuery(dishPageQueryDTO));
    }

    /**
     * 批量删除菜品
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam("ids") List<Long> ids) {
        log.info("批量删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品及口味
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询菜品：{}", id);
        return Result.success(dishService.getByIdWithFlavor(id));
    }

    /**
     * 修改菜品及口味
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品起售、停售
     */
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable("status") Integer status,
                                      @RequestParam("id") Long id) {
        log.info("菜品起售、停售：status={}, id={}", status, id);
        dishService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(@RequestParam("categoryId") Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        return Result.success(dishService.list(categoryId));
    }
}

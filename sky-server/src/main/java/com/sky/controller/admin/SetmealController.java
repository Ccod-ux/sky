package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    /** 套餐分页查询 */
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询：{}", setmealPageQueryDTO);
        return Result.success(setmealService.pageQuery(setmealPageQueryDTO));
    }

    /** 批量删除套餐 */
    @DeleteMapping
    public Result<String> delete(@RequestParam("ids") List<Long> ids) {
        log.info("批量删除套餐：{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /** 根据 id 查询套餐及其菜品 */
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable("id") Long id) {
        log.info("根据 id 查询套餐：{}", id);
        return Result.success(setmealService.getByIdWithDish(id));
    }

    /** 修改套餐 */
    @PutMapping
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /** 套餐起售、停售 */
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable("status") Integer status,
                                      @RequestParam("id") Long id) {
        log.info("套餐起售、停售：status={}, id={}", status, id);
        setmealService.startOrStop(status, id);
        return Result.success();
    }
}

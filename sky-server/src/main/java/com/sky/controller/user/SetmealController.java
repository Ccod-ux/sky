package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端套餐浏览接口
 */
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类 id 查询起售中的套餐
     */
    @GetMapping("/list")
    public Result<List<Setmeal>> list(@RequestParam("categoryId") Long categoryId) {
        log.info("用户端根据分类查询套餐：categoryId={}", categoryId);
        Setmeal setmeal = Setmeal.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return Result.success(setmealService.list(setmeal));
    }

    /**
     * 根据套餐 id 查询其中包含的菜品
     */
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> dishList(@PathVariable("id") Long id) {
        log.info("用户端查询套餐菜品：setmealId={}", id);
        return Result.success(setmealService.getDishItemById(id));
    }
}

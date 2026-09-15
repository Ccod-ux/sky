package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端购物车接口
 */
@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /** 查看当前用户的购物车 */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        log.info("查看当前用户购物车");
        return Result.success(shoppingCartService.showShoppingCart());
    }

    /** 清空当前用户的购物车 */
    @DeleteMapping("/clean")
    public Result<String> clean() {
        log.info("清空当前用户购物车");
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    /** 添加购物车中的商品 */
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车商品，商品信息：{}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /** 删除购物车中一个商品 */
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车商品，商品信息：{}", shoppingCartDTO);
        shoppingCartService.deleteShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}

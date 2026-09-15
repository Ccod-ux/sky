package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * 购物车业务层
 */
public interface ShoppingCartService {

    /** 查看当前用户的购物车 */
    List<ShoppingCart> showShoppingCart();

    /** 清空当前用户的购物车 */
    void cleanShoppingCart();

    /** 添加购物车中的商品 */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    void deleteShoppingCart(ShoppingCartDTO shoppingCartDTO);
}

package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 购物车数据访问层
 */
@Mapper
public interface ShoppingCartMapper {

    /** 动态条件查询购物车 */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /** 根据用户 id 清空购物车 */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /** 添加购物车中的商品 */
    void insert(ShoppingCart shoppingCart);

    /** 根据购物车记录 id 修改商品数量 */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /** 根据购物车记录 id 删除商品 */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);
}

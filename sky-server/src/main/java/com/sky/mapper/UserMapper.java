package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 用户数据访问层。
 */
@Mapper
public interface UserMapper {

    /** 根据微信 openid 查询用户 */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /** 根据ID查询用户 */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /** 新增用户 */
    void insert(User user);

    /**
     * 根据时间条件统计用户数量。
     */
    Integer countByMap(Map<String, Object> map);
}

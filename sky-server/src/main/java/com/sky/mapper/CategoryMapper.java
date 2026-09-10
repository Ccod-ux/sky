package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 新增分类
     * @param category
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values(#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Category category);

    /**
     * 根据id更新分类状态
     * @param category 分类状态和id
     */
    @AutoFill(OperationType.UPDATE)
    @Update("update category set status = #{status}, update_time = #{updateTime}, " +
            "update_user = #{updateUser} where id = #{id}")
    void updateStatus(Category category);

    /**
     * 根据类型查询分类
     * @param type 分类类型，允许不传
     * @return
     */
    List<Category> list(Integer type);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
}

package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 套餐数据访问层，目前用于菜品停售时同步停售关联套餐。
 */
@Mapper
public interface SetmealMapper {

    /** 更新套餐 */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /** 新增套餐 */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /** 套餐分页查询 */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /** 根据 id 查询套餐 */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /** 根据 id 删除套餐 */
    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long id);

    /** 根据 id 查询套餐及其菜品 */
    SetmealVO getByIdWithDish(Long id);

    /** 动态条件查询套餐 */
    List<Setmeal> list(Setmeal setmeal);

    /** 根据套餐 id 查询套餐内的菜品 */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}

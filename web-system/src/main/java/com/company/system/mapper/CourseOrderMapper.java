package com.company.system.mapper;

import java.util.List;
import com.company.common.core.mybaits.CustomBaseMapper;
import com.company.system.domain.CourseOrder;
import org.apache.ibatis.annotations.Param;

/**
 * 课程购买订单Mapper接口
 *
 * @author fei
 * @date 2021-04-04
 */
public interface CourseOrderMapper extends CustomBaseMapper<CourseOrder> {

    /**
     * 查询课程购买订单
     *
     * @param id 课程购买订单ID
     * @return 课程购买订单
     */
    CourseOrder selectCourseOrderById(Long id);

    /**
     * 查询课程购买订单列表
     *
     * @param courseOrder 课程购买订单
     * @return 课程购买订单集合
     */
    List<CourseOrder> selectCourseOrderList(@Param("order") CourseOrder order, @Param("userId") Long userId);

    /**
     * 新增课程购买订单
     *
     * @param courseOrder 课程购买订单
     * @return 结果
     */
    int insertCourseOrder(CourseOrder courseOrder);

    /**
     * 修改课程购买订单
     *
     * @param courseOrder 课程购买订单
     * @return 结果
     */
    int updateCourseOrder(CourseOrder courseOrder);

    /**
     * 删除课程购买订单
     *
     * @param id 课程购买订单ID
     * @return 结果
     */
    int deleteCourseOrderById(Long id);

    /**
     * 批量删除课程购买订单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCourseOrderByIds(Long[] ids);

}

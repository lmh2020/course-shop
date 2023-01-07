package com.company.system.service;

import java.util.List;
import com.company.system.domain.CourseOrder;
import com.company.system.form.CourseOrderForm;

/**
 * 课程购买订单Service接口
 *
 * @author fei
 * @date 2021-04-04
 */
public interface ICourseOrderService {
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
    List<CourseOrder> selectCourseOrderList(CourseOrder courseOrder);

    /**
     * 新增课程购买订单
     *
     * @param courseOrder 课程购买订单
     * @return 结果
     */
    void insertCourseOrder(CourseOrderForm form);

    /**
     * 修改课程购买订单
     *
     * @param courseOrder 课程购买订单
     * @return 结果
     */
    int updateCourseOrder(CourseOrder courseOrder);

    /**
     * 批量删除课程购买订单
     *
     * @param ids 需要删除的课程购买订单ID
     * @return 结果
     */
    int deleteCourseOrderByIds(Long[] ids);

    /**
     * 删除课程购买订单信息
     *
     * @param id 课程购买订单ID
     * @return 结果
     */
    int deleteCourseOrderById(Long id);

}

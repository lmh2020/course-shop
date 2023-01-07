package com.company.system.service;

import java.util.List;
import com.company.system.domain.Course;

/**
 * 课程Service接口
 */
public interface ICourseService {
    /**
     * 查询课程
     *
     * @param id 课程ID
     * @return 课程
     */
    Course selectCourseById(Long id);

    /**
     * 查询课程列表
     *
     * @param course 课程
     * @return 课程集合
     */
    List<Course> selectCourseList(Course course);

    List<Course> getAll();

    /**
     * 新增课程
     *
     * @param course 课程
     * @return 结果
     */
    int insertCourse(Course course);

    /**
     * 修改课程
     *
     * @param course 课程
     * @return 结果
     */
    int updateCourse(Course course);

    void updateSales(Long courseId);

    void updateViews(Long courseId);

    /**
     * 批量删除课程
     *
     * @param ids 需要删除的课程ID
     * @return 结果
     */
    int deleteCourseByIds(Long[] ids);

    /**
     * 删除课程信息
     *
     * @param id 课程ID
     * @return 结果
     */
    int deleteCourseById(Long id);

}

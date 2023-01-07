package com.company.system.mapper;

import java.util.List;
import com.company.common.core.mybaits.CustomBaseMapper;
import com.company.system.domain.Course;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 课程Mapper接口
 */
public interface CourseMapper extends CustomBaseMapper<Course> {

    /**
     * 查询课程
     *
     * @param id 课程ID
     * @return 课程
     */
    Course selectCourseById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 查询课程列表
     *
     * @param course 课程
     * @return 课程集合
     */
    List<Course> selectCourseList(@Param("course") Course course, @Param("userId") Long userId);

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

    /**
     * 删除课程
     *
     * @param id 课程ID
     * @return 结果
     */
    int deleteCourseById(Long id);

    /**
     * 批量删除课程
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCourseByIds(Long[] ids);

    @Update("UPDATE course SET sales = sales + 1 WHERE id = #{courseId}")
    void updateSales(Long courseId);

    @Update("UPDATE course SET views = views + 1 WHERE id = #{courseId}")
    void updateViews(Long courseId);

}

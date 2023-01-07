package com.company.system.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.company.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.system.mapper.CourseMapper;
import com.company.system.domain.Course;
import com.company.system.service.ICourseService;

/**
 * 课程Service业务层处理
 *
 * @author fei
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 查询课程
     *
     * @param id 课程ID
     * @return 课程
     */
    @Override
    public Course selectCourseById(Long id) {
        return courseMapper.selectCourseById(id, SecurityUtils.getLoginUserId());
    }

    /**
     * 查询课程列表
     *
     * @param course 课程
     * @return 课程
     */
    @Override
    public List<Course> selectCourseList(Course course) {
        return courseMapper.selectCourseList(course, SecurityUtils.getLoginUserId());
    }

    @Override
    public List<Course> getAll(){
        return this.list();
    }

    /**
     * 新增课程
     *
     * @param course 课程
     * @return 结果
     */
    @Override
    public int insertCourse(Course course) {
        course.setUserId(SecurityUtils.getLoginUserId());
        course.setCreateTime(LocalDateTime.now());
        return courseMapper.insertCourse(course);
    }

    /**
     * 修改课程
     *
     * @param course 课程
     * @return 结果
     */
    @Override
    public int updateCourse(Course course) {
        return courseMapper.updateCourse(course);
    }

    @Override
    public void updateSales(Long courseId){
        this.baseMapper.updateSales(courseId);
    }

    @Override
    public void updateViews(Long courseId){
        this.baseMapper.updateViews(courseId);
    }

    /**
     * 批量删除课程
     *
     * @param ids 需要删除的课程ID
     * @return 结果
     */
    @Override
    public int deleteCourseByIds(Long[] ids) {
        return courseMapper.deleteCourseByIds(ids);
    }

    /**
     * 删除课程信息
     *
     * @param id 课程ID
     * @return 结果
     */
    @Override
    public int deleteCourseById(Long id) {
        return courseMapper.deleteCourseById(id);
    }

}

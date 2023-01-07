package com.company.web.controller.system;

import java.util.List;

import com.company.common.core.domain.R;
import com.company.common.validator.group.ValidGroup;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.company.common.annotation.Log;
import com.company.common.core.controller.BaseController;
import com.company.common.enums.BusinessType;
import com.company.system.domain.Course;
import com.company.system.service.ICourseService;
import com.company.common.utils.poi.ExcelUtil;
import com.company.common.core.page.TableDataInfo;

/**
 * 课程Controller
 */
@RestController
@RequestMapping("system/course")
public class CourseController extends BaseController {

    @Autowired
    private ICourseService courseService;

    /**
     * 查询课程分页列表
     */
//    @PreAuthorize("@ss.hasPermi('system:course:list')")
    @GetMapping("page")
    public TableDataInfo page(Course course) {
        startPage();
        List<Course> list = courseService.selectCourseList(course);
        return getDataTable(list);
    }

    @GetMapping("all")
    public R getAll() {
        List<Course> list = courseService.getAll();
        return R.successWithData(list);
    }

    /**
     * 导出课程列表
     */
    @PreAuthorize("@ss.hasPermi('system:course:export')")
    @Log(title = "课程", businessType = BusinessType.EXPORT)
    @GetMapping("export")
    public R export(Course course) {
        List<Course> list = courseService.selectCourseList(course);
        ExcelUtil<Course> util = new ExcelUtil<Course>(Course.class);
        return R.successWithData(util.exportExcel(list, "course"));
    }

    /**
     * 获取课程详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:course:query')")
    @GetMapping("{id}")
    public R getInfo(@PathVariable("id") Long id) {
        return R.successWithData(courseService.selectCourseById(id));
    }

    /**
     * 新增课程
     */
    @PreAuthorize("@ss.hasPermi('system:course:add')")
    @Log(title = "课程", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody @Validated(ValidGroup.Insert.class) Course course) {
        return super.doSuccess(courseService.insertCourse(course));
    }

    /**
     * 修改课程
     */
    @PreAuthorize("@ss.hasPermi('system:course:edit')")
    @Log(title = "课程", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(ValidGroup.Update.class) Course course) {
        return super.doSuccess(courseService.updateCourse(course));
    }

    /**
     * 删除课程
     */
    @PreAuthorize("@ss.hasPermi('system:course:remove')")
    @Log(title = "课程", businessType = BusinessType.DELETE)
	@DeleteMapping("{ids}")
    public R remove(@PathVariable Long[] ids) {
        return super.doSuccess(courseService.deleteCourseByIds(ids));
    }

    @GetMapping("views")
    public R updateViews(@RequestParam Long courseId){
        courseService.updateViews(courseId);
        return R.success();
    }

}

    package com.company.web.controller.system;

import java.util.List;

import com.company.common.core.domain.R;
import com.company.common.kuaidiniao.KdniaoTrackQueryAPI;
import com.company.system.form.CourseOrderForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.company.common.annotation.Log;
import com.company.common.core.controller.BaseController;
import com.company.common.enums.BusinessType;
import com.company.system.domain.CourseOrder;
import com.company.system.service.ICourseOrderService;
import com.company.common.utils.poi.ExcelUtil;
import com.company.common.core.page.TableDataInfo;

/**
 * 课程购买订单Controller
 *
 * @author fei
 * @date 2021-04-04
 */
@RestController
@RequestMapping("system/order")
public class CourseOrderController extends BaseController {

    @Autowired
    private ICourseOrderService courseOrderService;

    @Autowired
    private KdniaoTrackQueryAPI kdniaoTrackQueryAPI;

    /**
     * 查询课程购买订单分页列表
     */
    @PreAuthorize("@ss.hasPermi('system:order:list')")
    @GetMapping("page")
    public TableDataInfo page(CourseOrder courseOrder) {
        startPage();
        List<CourseOrder> list = courseOrderService.selectCourseOrderList(courseOrder);
        return getDataTable(list);
    }

    /**
     * 导出课程购买订单列表
     */
    @PreAuthorize("@ss.hasPermi('system:order:export')")
    @Log(title = "课程购买订单", businessType = BusinessType.EXPORT)
    @GetMapping("export")
    public R export(CourseOrder courseOrder) {
        List<CourseOrder> list = courseOrderService.selectCourseOrderList(courseOrder);
        ExcelUtil<CourseOrder> util = new ExcelUtil<CourseOrder>(CourseOrder.class);
        return R.successWithData(util.exportExcel(list, "order"));
    }

    @GetMapping("deliver")
    public R getDeliver(@RequestParam String deliverNumber) throws Exception {
        String res = kdniaoTrackQueryAPI.getOrderTracesByJson("ANE", deliverNumber);
        return R.successWithData(res);
    }

    /**
     * 获取课程购买订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:order:query')")
    @GetMapping("{id}")
    public R getInfo(@PathVariable("id") Long id) {
        return R.successWithData(courseOrderService.selectCourseOrderById(id));
    }

    /**
     * 新增课程购买订单
     */
    @PreAuthorize("@ss.hasPermi('system:order:add')")
    @Log(title = "课程购买订单", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody @Validated CourseOrderForm form) {
        courseOrderService.insertCourseOrder(form);
        return R.success();
    }

    /**
     * 修改课程购买订单
     */
    @PreAuthorize("@ss.hasPermi('system:order:edit')")
    @Log(title = "课程购买订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody CourseOrder courseOrder) {
        return super.doSuccess(courseOrderService.updateCourseOrder(courseOrder));
    }

    /**
     * 删除课程购买订单
     */
    @PreAuthorize("@ss.hasPermi('system:order:remove')")
    @Log(title = "课程购买订单", businessType = BusinessType.DELETE)
	@DeleteMapping("{ids}")
    public R remove(@PathVariable Long[] ids) {
        return super.doSuccess(courseOrderService.deleteCourseOrderByIds(ids));
    }

}

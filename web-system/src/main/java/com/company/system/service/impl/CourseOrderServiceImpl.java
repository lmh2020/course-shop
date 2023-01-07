package com.company.system.service.impl;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.company.common.core.domain.entity.SysUser;
import com.company.common.exception.ServiceException;
import com.company.common.utils.AssertUtil;
import com.company.common.utils.SecurityUtils;
import com.company.common.utils.uuid.UUID;
import com.company.system.domain.Course;
import com.company.system.form.CourseOrderForm;
import com.company.system.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.system.mapper.CourseOrderMapper;
import com.company.system.domain.CourseOrder;
import com.company.system.service.ICourseOrderService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 课程购买订单Service业务层处理
 *
 * @author fei
 * @date 2021-04-04
 */
@Service
public class CourseOrderServiceImpl extends ServiceImpl<CourseOrderMapper, CourseOrder> implements ICourseOrderService {

    @Autowired
    private CourseOrderMapper courseOrderMapper;

    @Autowired
    private ICourseService courseService;

    /**
     * 查询课程购买订单
     *
     * @param id 课程购买订单ID
     * @return 课程购买订单
     */
    @Override
    public CourseOrder selectCourseOrderById(Long id) {
        return courseOrderMapper.selectCourseOrderById(id);
    }

    /**
     * 查询课程购买订单列表
     *
     * @param courseOrder 课程购买订单
     * @return 课程购买订单
     */
    @Override
    public List<CourseOrder> selectCourseOrderList(CourseOrder courseOrder) {
        return courseOrderMapper.selectCourseOrderList(courseOrder, SecurityUtils.getLoginUserId());
    }

    /**
     * 新增课程购买订单
     *
     * @param form 课程购买订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCourseOrder(CourseOrderForm form) {
        Course course = Optional.ofNullable(courseService.selectCourseById(form.getCourseId()))
                .orElseThrow(() -> new ServiceException("课程信息不存在"));
        /* 判断是否已购买 */
        SysUser user = SecurityUtils.getLoginUser().getUser();
        int count = this.count(new QueryWrapper<CourseOrder>().lambda()
                .eq(CourseOrder::getCourseId, form.getCourseId())
                .eq(CourseOrder::getUserId, user.getUserId())
                .last("LIMIT 1"));
        AssertUtil.isTrue(count > 0, "您已购买过该课程，请勿重复购买");

        /* 保存 */
        CourseOrder courseOrder = new CourseOrder();
        courseOrder.setCourseId(form.getCourseId());
        courseOrder.setUserId(user.getUserId());
        courseOrder.setUserName(user.getNickName());
        courseOrder.setCourseName(course.getName());
        courseOrder.setAddress(form.getAddress());
        courseOrder.setGift(form.getGift());
        courseOrder.setPrice(course.getPrice());
        courseOrder.setSerialNumber(UUID.fastUUID().toString());
        courseOrder.setDeliverNumber("210001633605");
        courseOrder.setDeliverChannel("ANE");
        this.save(courseOrder);

        /* 更新课程销量 */
        courseService.updateSales(form.getCourseId());
    }

    /**
     * 修改课程购买订单
     *
     * @param courseOrder 课程购买订单
     * @return 结果
     */
    @Override
    public int updateCourseOrder(CourseOrder courseOrder) {
        return courseOrderMapper.updateCourseOrder(courseOrder);
    }

    /**
     * 批量删除课程购买订单
     *
     * @param ids 需要删除的课程购买订单ID
     * @return 结果
     */
    @Override
    public int deleteCourseOrderByIds(Long[] ids) {
        return courseOrderMapper.deleteCourseOrderByIds(ids);
    }

    /**
     * 删除课程购买订单信息
     *
     * @param id 课程购买订单ID
     * @return 结果
     */
    @Override
    public int deleteCourseOrderById(Long id) {
        return courseOrderMapper.deleteCourseOrderById(id);
    }

}

package com.company.system.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.company.common.annotation.Excel;
import com.company.common.core.domain.entity.SysUser;
import com.company.common.enums.CommonBooleanEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程购买订单对象 course_order
 *
 * @author fei
 * @date 2021-04-04
 */
@Data
public class CourseOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 购买用户ID */
    private Long userId;

    /** 课程ID */
    private Long courseId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private String userName;

    /** 礼品 */
    @Excel(name = "礼品")
    private String gift;

    /** 购买价格 */
    @Excel(name = "购买价格")
    private Long price;

    /** 课程名称 */
    @Excel(name = "课程名称")
    private String courseName;

    /** 发货地址 */
    @Excel(name = "发货地址")
    private String address;

    /** 发货渠道 */
    @Excel(name = "发货渠道")
    private String deliverChannel;

    /** 物流单号 */
    @Excel(name = "物流单号")
    private String deliverNumber;

    /** 支付流水号 */
    private String serialNumber;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private SysUser user;

    private transient String isMy;


}

package com.company.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.company.common.annotation.Excel;
import com.company.common.core.domain.BaseEntity;
import com.company.common.core.domain.entity.SysUser;
import com.company.common.validator.group.ValidGroup;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 课程对象 course
 */
@Data
public class Course extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @NotNull(groups = ValidGroup.Update.class, message = "编号不能为空")
    private Long id;

    /** 课程所属用户（谁上传的课程） */
    private Long userId;

    /** 课程测试 */
    private Long examId;

    /** 访问链接 */
    @NotBlank(groups = ValidGroup.Merge.class, message = "访问链接不能为空")
    private String linkUrl;

    /** 课程名 */
    @Excel(name = "课程名")
    @NotBlank(groups = ValidGroup.Merge.class, message = "课程名不能为空")
    private String name;

    /** 标题 */
    @Excel(name = "标题")
    @NotBlank(groups = ValidGroup.Merge.class, message = "标题不能为空")
    private String title;

    /** 封面图 */
    @NotBlank(groups = ValidGroup.Merge.class, message = "封面图不能为空")
    private String img;

    /** 说明 */
    private String detail;

    /** 价格 */
    @Excel(name = "价格")
    @NotNull(groups = ValidGroup.Merge.class, message = "封面图不能为空")
    @Min(groups = ValidGroup.Merge.class, value = 1, message = "售价不能低于1元")
    @Max(groups = ValidGroup.Merge.class, value = 999, message = "售价不能高于999元")
    private Long price;

    private Integer sales;

    private Integer views;

    /** 礼物 */
    private String gift;

    @TableField(exist = false)
    private SysUser user;

    @TableField(exist = false)
    private CourseOrder order;

    private transient String isMy;

    private transient String buyed;

}

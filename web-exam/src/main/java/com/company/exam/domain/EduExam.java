package com.company.exam.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.company.common.annotation.Excel;
import com.company.common.core.domain.BaseEntity;
import com.company.common.enums.business.ExamRecordStateEnum;
import com.company.common.enums.business.ExamStateEnum;
import com.company.system.domain.Course;
import lombok.Data;

/**
 * 考核对象 edu_exam
 */
@Data
public class EduExam extends BaseEntity{

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 考核标题 */
    @Excel(name = "考核标题")
    private String title;

    /** 封面 */
    @Excel(name = "封面")
    private String coverImg;

    /** 总分 */
    @Excel(name = "总分")
    private Integer totalScore;

    /** 困难等级 */
    @Excel(name = "考试时间", readConverterExp = "1~5")
    private Integer difficultyLevel;

    /** 状态 */
    @Excel(name = "状态")
    private ExamStateEnum state;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    private String delFlag;

    @Excel(name = "备注")
    private String remark;

    @TableField(exist = false)
    private Long recordCollectId;

    @TableField(exist = false)
    private ExamRecordStateEnum recordState;

    @TableField(exist = false)
    private String courseName;

    private Long courseId;


}

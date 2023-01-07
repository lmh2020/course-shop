package com.company.exam.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.company.common.annotation.Excel;
import com.company.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 考核试卷基础信息对象 edu_exam_paper
 */
@Data
public class EduPaperStore extends BaseEntity {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 满分 */
    @Excel(name = "满分")
    private Integer totalScore;

    /** 试卷说明 */
    @Excel(name = "试卷说明")
    private String detail;

    /** 困难等级（1~5） */
    @Excel(name = "困难等级", readConverterExp = "1=~5")
    private Integer difficultyLevel;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    private String delFlag;


}

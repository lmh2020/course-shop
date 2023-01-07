package com.company.exam.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 考核相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "exam")
public class EduExamConfig {

    /* 考核允许修改的时间（距离考核开始，单位：分钟） */
    private Long operateAllowTime;

    /* 交卷时，实际交卷时间与系统规定交卷时间的最大延迟容忍误差（单位：秒） */
    private Long submitAllowTime;

    /* 允许提前开考的时间（单位：分钟） */
    private Long earlierExamineAllowTime;

    /* 考试结束提前提醒客户端的时间（单位：秒） */
    private Long earlierSubmitNotifyTime;

}

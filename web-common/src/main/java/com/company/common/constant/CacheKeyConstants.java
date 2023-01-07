package com.company.common.constant;

/**
 * @Description  系统缓存（Redis等）常量
 */
public class CacheKeyConstants {

    /* 应用全局统一前缀（所有缓存都必须加上该前缀） */
    public static final String GLOBAL_APPLICATION_PREFIX = "erp:";


    /* ############################################### 教育业务【edu】 ######################################## */

    /* 教育业务通用前缀 */
    public static final String EDU_PREFIX = GLOBAL_APPLICATION_PREFIX + "edu:";


    /* 用户定时答案缓存-前缀 */
    public static final String USER_ANSWER = EDU_PREFIX + "exam:answers:user:";

}

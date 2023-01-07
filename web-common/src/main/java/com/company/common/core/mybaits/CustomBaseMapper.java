package com.company.common.core.mybaits;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description
 */
public interface CustomBaseMapper<T> extends BaseMapper<T> {

    /**
     * @description 根据数据库某字段值判断记录是否存在
     * 执行SQL： SELECT CASE COUNT(1) WHEN 0 THEN 0 ELSE 1 END FROM tableName WHERE ${columnName} = #{columnValue} LIMIT 1
     * @param  columnName   数据库字段名称
     * @param  columnValue   columnName属性对应的值
     */
    Boolean exist(@Param("columnName") String columnName, @Param("columnValue") String columnValue);

}

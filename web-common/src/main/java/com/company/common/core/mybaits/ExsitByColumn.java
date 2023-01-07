package com.company.common.core.mybaits;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;


/**
 * @Description SQL注入器，用于构造mybaits-plus的通用查询方法，根据表某字段值判断表中是否存在至少一条记录
 */
public class ExsitByColumn extends AbstractMethod {

    /**
     * 注入自定义 MappedStatement
     *
     * @param mapperClass mapper 接口
     * @param modelClass  mapper 泛型
     * @param tableInfo   数据库表反射信息
     */
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sqlScript;
        String logicDeleteSql = tableInfo.getLogicDeleteSql(true, true);
        if (tableInfo.isLogicDelete()){
            sqlScript = "<script>SELECT CASE COUNT(1) WHEN 0 THEN 0 ELSE 1 END FROM %s WHERE ${columnName} = #{columnValue} " + logicDeleteSql + " LIMIT 1</script>";
        } else {
            sqlScript = "<script>SELECT CASE COUNT(1) WHEN 0 THEN 0 ELSE 1 END FROM %s WHERE ${columnName} = #{columnValue} LIMIT 1</script>";
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, String.format(sqlScript, tableInfo.getTableName()), Object.class);
        return this.addSelectMappedStatementForOther(mapperClass, "exist", sqlSource, Boolean.class);
    }

}

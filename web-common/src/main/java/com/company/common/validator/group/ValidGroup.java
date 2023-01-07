package com.company.common.validator.group;

/**
 * @Description 校验组
 */
public class ValidGroup {

    /* 修改/添加 */
    public interface Merge {}

    /* 添加 */
    public interface Insert extends Merge{}

    /* 修改 */
    public interface Update extends Merge{}

}

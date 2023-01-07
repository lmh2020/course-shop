package com.company.common.utils.bean;

import com.company.common.exception.ServiceException;
import com.company.common.utils.AssertUtil;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @desc Bean属性赋值工具
 */
public class BeanTransUtil {

    public static <R, T> T copy(R r, Class<T> clazz){
        AssertUtil.isNull(r, "source object should not be null!");
        T instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(r, instance);
        return instance;
    }

    public static <R, T> T copy(R r, Class<T> clazz, String... ignores){
        AssertUtil.isNull(r, "source object should not be null!");
        T instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(r, instance, ignores);
        return instance;
    }

    public static <R, T> void copy(R r, T t){
        AssertUtil.isNull(r, "source object should not be null!");
        AssertUtil.isNull(t, "target object should not be null!");
        BeanUtils.copyProperties(r, t);
    }

    public static <R, T> void copy(R r, T t, String... ignores){
        AssertUtil.isNull(r, "source object should not be null!");
        AssertUtil.isNull(t, "target object should not be null!");
        BeanUtils.copyProperties(r, t, ignores);
    }

    public static <R, T> List<T> copy(Collection<R> collection, Class<T> clazz){
        AssertUtil.isEmpty(collection, "source collection should not be empty!");
        List result = new ArrayList(collection.size());
        try {
            if (collection.size() > 10000){
                collection.parallelStream().forEach(item -> result.add(copy(item, clazz)));
            } else {
                collection.stream().forEach(item -> result.add(copy(item, clazz)));
            }
        } catch (Exception e){
            throw new ServiceException("集合转换发生异常：" + e.getMessage());
        }
        return result;
    }


}

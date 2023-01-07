package com.company.common.utils;

import com.company.common.exception.ServiceException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 */
public class AssertUtil {

    public static void isTrue(Boolean expression, String errorMsg){
        if (expression){
            throw new ServiceException(errorMsg);
        }
    }

    public static void isFalse(Boolean expression, String errorMsg){
        isTrue(!expression, errorMsg);
    }

    public static void equels(Object val1, Object val2, String errorMsg){
        if (val1 == val2 || (val1 != null && val1.equals(val2))){
            throw new ServiceException(errorMsg);
        }
    }

    public static void notEquels(Object val1, Object val2, String errorMsg){
        if (val1 == null || !val1.equals(val2)){
            throw new ServiceException(errorMsg);
        }
    }

    public static void isNull(Object t, String errorMsg){
        if (t == null){
            throw new ServiceException(errorMsg);
        }
    }

    public static void isNotNull(Object t, String errorMsg){
        if (t != null){
            throw new ServiceException(errorMsg);
        }
    }

    public static void isEmpty(Collection collection, String errorMsg){
        if (CollectionUtils.isEmpty(collection)){
            throw new ServiceException(errorMsg);
        }
    }

    public static void isEmpty(Map map, String errorMsg){
        if (MapUtils.isEmpty(map)){
            throw new ServiceException(errorMsg);
        }
    }

    public static void isEmpty(Object[] arr, String errorMsg){
        if (ArrayUtils.isEmpty(arr)){
            throw new ServiceException(errorMsg);
        }
    }

    public static void isBlank(String val, String errorMsg){
        if (StringUtils.isBlank(val)){
            throw new ServiceException(errorMsg);
        }
    }

}

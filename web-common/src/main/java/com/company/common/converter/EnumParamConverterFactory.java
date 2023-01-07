package com.company.common.converter;

import com.company.common.enums.base.ICommonEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * @Description
 */
public final class EnumParamConverterFactory implements ConverterFactory<String, ICommonEnum> {

    @Override
    public <E extends ICommonEnum> Converter<String, E> getConverter(Class<E> targetType) {
        return new EnumParamConverter(targetType);
    }

    private static final class EnumParamConverter<E extends Enum<E> & ICommonEnum> implements Converter<String, E> {

        private final Class<E> targetType;

        public EnumParamConverter(Class<E> targetType) {
            this.targetType = targetType;
        }

        @Override
        public E convert(String val) {
            if (StringUtils.isBlank(val)){
                return null;
            }
            return ICommonEnum.assertContainsAndGet(targetType, val);
        }

    }

}

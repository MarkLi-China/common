package com.domain.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 * com.domain.common
 * @author Mark Li
 * @version 1.0.0
 * @since 2017/1/6
 */
public class CommonUtil {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    /**
     * 获取对象内的属性值
     * @param fieldName 属性名称
     * @param o         对象
     * @return 属性值
     */
    public static String getFieldValueByName(String fieldName, Object o) {

        try {
            Object obj;
            if (o instanceof Map) {
                Map map = (Map) o;
                obj = map.get(fieldName);
            } else {
                String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method method;
                try {
                    method = o.getClass().getMethod(methodName);
                } catch (NoSuchMethodException e) {
                    logger.warn(e.getMessage(), e);
                    methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    try {
                        method = o.getClass().getMethod(methodName);
                    } catch (NoSuchMethodException e1) {
                        logger.error(e1.getMessage(), e1);
                        return "";
                    }
                }
                try {
                    obj = method.invoke(o);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error(e.getMessage(), e);
                    return "";
                }
            }
            String text;
            if (obj instanceof Boolean) {
                boolean value = (boolean) obj;
                text = value ? "true" : "false";
            } else if (obj instanceof Date) {
                Date date = (Date) obj;
                text = DateUtil.formatDateTime(date);
            } else {
                text = obj.toString();
            }
            return text;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }

    public static void wipeOffNull(Object bean) throws Exception {

        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(bean.getClass());
        for (PropertyDescriptor pd : propertyDescriptors) {
            if (pd.getReadMethod().invoke(bean) != null) {
                continue;
            }
            if (pd.getPropertyType().equals(Integer.class)) {
                pd.getWriteMethod().invoke(bean, 0);
            } else if (pd.getPropertyType().equals(Long.class)) {
                pd.getWriteMethod().invoke(bean, 0L);
            } else if (pd.getPropertyType().equals(Byte.class)) {
                pd.getWriteMethod().invoke(bean, (byte) 0);
            } else if (pd.getPropertyType().equals(Short.class)) {
                pd.getWriteMethod().invoke(bean, (short) 0);
            } else if (pd.getPropertyType().equals(Double.class)) {
                pd.getWriteMethod().invoke(bean, (double) 0);
            } else if (pd.getPropertyType().equals(Float.class)) {
                pd.getWriteMethod().invoke(bean, (float) 0);
            } else if (pd.getPropertyType().equals(String.class)) {
                pd.getWriteMethod().invoke(bean, "");
            }
        }
    }
}

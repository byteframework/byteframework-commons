package com.byteframework.commons.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

public class MapUtils {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T getValue(Map map, String key, Class<T> clazz, boolean nullDefault) {
        if (map == null || map.isEmpty()) return getDefaultVal(clazz, nullDefault);
        Object obj = map.get(key);
        if (obj == null) return getDefaultVal(clazz, nullDefault);
        if (obj.getClass().isArray()) {
            obj = ((Object[]) obj)[0];
        }
        if (clazz.equals(Integer.class)) {
            return (T) Integer.valueOf(new BigDecimal(obj.toString()).intValue());
        } else if (clazz.equals(Long.class)) {
            return (T) Long.valueOf(new BigDecimal(obj.toString()).longValue());
        } else if (clazz.equals(Float.class)) {
            return (T) Float.valueOf(obj.toString());
        } else if (clazz.equals(Double.class)) {
            return (T) Double.valueOf(obj.toString());
        } else if (clazz.equals(String.class)) {
            return (T) obj.toString();
        } else {
            return (T) obj;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getDefaultVal(Class<T> clazz, boolean nullDefault) {
        if (!nullDefault) return null;
        if (clazz.equals(Integer.class)) {
            return (T) new Integer(0);
        } else if (clazz.equals(Long.class)) {
            return (T) new Long(0);
        } else if (clazz.equals(Float.class)) {
            return (T) new Float(0);
        } else if (clazz.equals(Double.class)) {
            return (T) new Double(0);
        } else {
            return null;
        }
    }


    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
    public static Map<String, Object> transBean2Map(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            // 过滤class属性
            if (!key.equals("class")) {
                // 得到property对应的getter方法
                Method getter = property.getReadMethod();
                Object value = getter.invoke(obj);
                map.put(key, value);
            }
        }
        return map;
    }


    public static List<Map<String, Object>> transList2Map(List<?> objs) throws Exception {
        if (objs == null || objs.isEmpty()) return null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Object obj : objs) {
            result.add(transBean2Map(obj));
        }
        return result;
    }


    @SuppressWarnings("rawtypes")
    public static <T> T convertMap(Class<T> type, Map map) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        return setPropertiesVal(type, propertyDescriptors, map);
    }

    @SuppressWarnings("rawtypes")
    private static <T> T setPropertiesVal(Class<T> type, PropertyDescriptor[] propertyDescriptors, Map map)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        T obj = type.newInstance(); // 创建 JavaBean 对象
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!map.containsKey(propertyName)) continue;
            Object value = map.get(propertyName);
            if (value == null) continue;

            Object[] args = null;
            if (descriptor.getPropertyType().equals(Integer.class)) {
                args = new Integer[]{Integer.valueOf(value.toString())};
            } else if (descriptor.getPropertyType().equals(Float.class)) {
                args = new Float[]{Float.valueOf(value.toString())};
            } else if (descriptor.getPropertyType().equals(Double.class)) {
                args = new Double[]{Double.valueOf(value.toString())};
            } else if (descriptor.getPropertyType().equals(Long.class)) {
                args = new Long[]{Long.valueOf(value.toString())};
            } else if (descriptor.getPropertyType().equals(String.class)) {
                args = new String[]{value.toString()};
            } else if (descriptor.getPropertyType().equals(Date.class)) {
                String sVal = value.toString();
                args = new Object[1];
                if (sVal.length() == 10) {
                    args[0] = DateUtils.parse(sVal, DateUtils.DATE_FORMAT);
                } else if (sVal.length() == 19) {
                    args[0] = DateUtils.parse(sVal, DateUtils.DATE_TIME_FORMAT);
                }
            }
            descriptor.getWriteMethod().invoke(obj, args);
        }
        return obj;
    }

    @SuppressWarnings("rawtypes")
    public static <T> List<T> convertMaps(Class<T> type, List<Map> list) throws Exception {
        if (list == null || list.isEmpty()) return null;
        List<T> result = new ArrayList<T>();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (Map map : list) {
            result.add(setPropertiesVal(type, propertyDescriptors, map));
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public static <T> List<T> Maps(Class<T> type, List<Map> list) throws Exception {
        if (list == null || list.isEmpty()) return null;
        List<T> result = new ArrayList<T>();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (Map map : list) {
            result.add(setPropertiesVal(type, propertyDescriptors, map));
        }
        return result;
    }

    //map转class
    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass) throws Exception {
        if (map == null) return null;

        T obj = beanClass.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            Method setter = property.getWriteMethod();
            if (setter != null) {
                setter.invoke(obj, map.get(property.getName()));
            }
        }
        return obj;
    }
}

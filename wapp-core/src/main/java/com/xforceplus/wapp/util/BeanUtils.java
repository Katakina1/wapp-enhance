package com.xforceplus.wapp.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeansException;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Describe: 对象拷贝
 * @Author xiezhongyong
 * @Date 2019-12-11
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {
    /**
     * 对象拷贝
     *
     * @param source 数据来源
     * @param target 目标对象 （不能为Null）
     * @throws BeansException
     */
    public static void copyProperties(Object source, Object target) throws BeansException {
        baseCopyProperties(source, target, null);
    }

    /**
     * 对象拷贝
     *
     * @param source           数据来源
     * @param target           目标对象 （不能为Null）
     * @param ignoreProperties 忽略属性
     * @throws BeansException
     */
    public static <T> void copyProperties(Object source, Object target, TypeFunctional<T>... ignoreProperties) throws BeansException {

        baseCopyProperties(source, target, null, getIgnorePropertieNames(ignoreProperties));
    }

    /**
     * 对象拷贝
     *
     * @param source      数据来源
     * @param targetClass 目标类型对象
     * @throws BeansException
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass, TypeFunctional<T>... ignoreProperties) throws BeansException {
        T target = null;
        try {
            if (null == source) {
                return null;
            }
            target = targetClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        baseCopyProperties(source, target, null, getIgnorePropertieNames(ignoreProperties));

        return target;
    }

    /**
     * 对象拷贝
     *
     * @param source      数据来源
     * @param targetClass 目标类型对象
     * @throws BeansException
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) throws BeansException {

        T target = null;
        try {
            if (null == source) {
                return null;
            }
            target = targetClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        baseCopyProperties(source, target, null);

        return target;
    }

    /**
     * List拷贝
     *
     * @param sourceList  数据来源
     * @param targetClass 目标类型对象
     * @throws BeansException
     */
    public static <E, T> List<T> copyList(List<E> sourceList, Class<T> targetClass) throws BeansException {
        List<T> targetList = new ArrayList<>();

        if (null == sourceList || null == targetClass) {
            return targetList;
        }
        for (E sourceObj : sourceList) {
            T targetObj = copyProperties(sourceObj, targetClass);
            targetList.add(targetObj);
        }
        return targetList;


    }

    /**
     * 对象拷贝(支持list 拷贝)
     * 1、支持List 属性拷贝
     * 2、不支持Null 属性拷贝
     *
     * @param source
     * @param target
     * @param targetListFieldTypes target list 属性类型，如 target 属性名为 dataList<User> 传值为 key=dataList;value=User.class
     * @throws BeansException
     */
    public static void copyPropertiesSpList(Object source, Object target, Map<String, Class> targetListFieldTypes) throws BeansException {
        baseCopyProperties(source, target, targetListFieldTypes);
    }

    /**
     * 对象拷贝(支持list 拷贝)
     * 1、支持List 属性拷贝
     * 2、不支持Null 属性拷贝
     *
     * @param source
     * @param target
     * @param targetListFieldTypes target list 属性类型，如 target 属性名为 dataList<User> 传值为 key=dataList;value=User.class
     * @throws BeansException
     */
    private static void baseCopyProperties(Object source, Object target, Map<String, Class> targetListFieldTypes) throws BeansException {
        baseCopyProperties(source, target, targetListFieldTypes, null);
    }

    /**
     * 对象拷贝(支持list 拷贝)
     * 1、支持List 属性拷贝
     * 2、不支持Null 属性拷贝
     *
     * @param source
     * @param target
     * @param targetListFieldTypes target list 属性类型，如 target 属性名为 dataList<User> 传值为 key=dataList;value=User.class
     * @throws BeansException
     */
    private static void baseCopyProperties(Object source, Object target, Map<String, Class> targetListFieldTypes,
                                           List<String> ignorePropertieNames) throws BeansException {
        if (null == source || null == target) {
            return;
        }
        PropertyDescriptor[] sourcePds = BeanUtils.getPropertyDescriptors(source.getClass());
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(target.getClass());

        HashMap<String, PropertyDescriptor> targetMaps = new HashMap<>(20);

        for (PropertyDescriptor targetPd : targetPds) {
            targetMaps.put(targetPd.getName(), targetPd);
        }
        for (PropertyDescriptor sourcePd : sourcePds) {
            if (null != ignorePropertieNames && ignorePropertieNames.contains(sourcePd.getName())) {
                continue;
            }

            Method readMethod = sourcePd.getReadMethod();
            if (null == readMethod) {
                continue;
            }
            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                readMethod.setAccessible(true);
            }
            Object sourceValue = null;
            try {
                sourceValue = readMethod.invoke(source);
            } catch (Exception e) {
            }
            if (null == sourceValue) {
                continue;
            }

            // 时间格式处理
            sourceValue = convertDateTime(source, sourcePd, sourceValue);

            // json 字符串转 对象
            sourceValue = jsonStr2Obj(source, sourcePd, sourceValue);

            PropertyDescriptor targetPd = targetMaps.get(sourcePd.getName());


            if (null == targetPd) {
                continue;
            }

//            sourceValue = value2Enum(target, targetPd, sourceValue);

            Method writeMethod = targetPd.getWriteMethod();
            if (null == writeMethod) {
                continue;
            }
            Class paramType = null;
            try {
                paramType = writeMethod.getParameters()[0].getType();
            } catch (Exception e) {
            }
            if (null == paramType) {
                continue;
            }
            if (paramType != sourceValue.getClass()) {
                Object customSourceValue = getCustomSourceValue(sourceValue, target, paramType, targetPd,
                        targetListFieldTypes);
                if (null == customSourceValue) {
                    continue;
                }
                sourceValue = customSourceValue;

            }
            try {
                writeMethod.invoke(target, sourceValue);
            } catch (Exception e) {
            }

        }
    }


    /**
     * 自定义参数特殊处理
     *
     * @param sourceValue
     * @param target
     * @param paramType
     * @param targetPd
     * @param targetListFieldTypes
     * @return
     */
    private static Object getCustomSourceValue(Object sourceValue, Object target, Class paramType,
                                               PropertyDescriptor targetPd, Map<String, Class> targetListFieldTypes) {
        if (paramType.equals(BigDecimal.class)) {
            return new BigDecimal(sourceValue.toString());
        }

        if (paramType.equals(Long.class)) {
            return Long.valueOf(sourceValue.toString());
        }

        if (paramType.equals(Integer.class)) {
            return Integer.valueOf(sourceValue.toString());
        }

        if (paramType.equals(Integer.TYPE)) {
            return Integer.valueOf(sourceValue.toString());
        }
        // 时间类型转换为 时间戳(必须在String.class 之前)
        if (paramType.equals(String.class) && sourceValue instanceof Date) {
            String dateStr = String.valueOf(((Date) sourceValue).getTime());
            // DB 默认数据处理
            if ("14400000".equals(dateStr)) {
                dateStr = "";
            }
            return dateStr;
        }
        // 时间类型转换为 时间戳(必须在String.class 之前)
        if (paramType.equals(String.class) && sourceValue instanceof LocalDateTime) {
            String dateStr =
                    String.valueOf(((LocalDateTime) sourceValue).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            // DB 默认数据处理
            if ("14400000".equals(dateStr)) {
                dateStr = "";
            }
            return dateStr;
        }
        // 时间类型转换为 时间戳(必须在String.class 之前)
        if (paramType.equals(String.class) && sourceValue instanceof LocalDateTime) {
            return String.valueOf(((LocalDateTime) sourceValue).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        }

        if (paramType.equals(String.class)) {
            return sourceValue.toString();
        }
        // List 属性特殊处理
        if (sourceValue instanceof List && paramType == List.class) {
            Class aClass = null;
            if (null != targetListFieldTypes) {

                aClass = targetListFieldTypes.get(targetPd.getName());
            }
            if (null == aClass && isListDepthCopy(target, targetPd)) {
                // 只有 target 带有 @ListDepth 注解才会进行深度下钻list
                aClass = getListGenericType(target.getClass(), targetPd.getName());
            }
            if (null == aClass) {
                // 未指定对应的list类型 直接跳过赋值
                return sourceValue;
            }
            List sourceList = (List) sourceValue;
            List targetList = new ArrayList();
            for (Object sourceObj : sourceList) {
                try {
                    Object targetObj = aClass.newInstance();
                    // 循环拷贝 list 对象
                    copyPropertiesSpList(sourceObj, targetObj, null);
                    targetList.add(targetObj);
                } catch (Exception e) {
                }

            }
            return targetList;

        }
        try {
            // 对象类型不一致处理
            Object targetObj = paramType.newInstance();
            copyPropertiesSpList(sourceValue, targetObj, null);
            return targetObj;
        } catch (Exception e) {
        }

        return sourceValue;
    }

    /**
     * 获取list 泛型类型
     *
     * @return
     */
    private static Class getListGenericType(Class aClass, String fieldName) {
        try {
            Field field = aClass.getDeclaredField(fieldName);
            // 设置字段可访问（必须，否则报错）
            field.setAccessible(true);
            Class<?> curFieldType = field.getType();

            if (curFieldType.equals(List.class)) {
                // 当前集合的泛型类型
                Type genericType = field.getGenericType();
                if (null == genericType) {
                    return null;
                }
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    // 得到泛型里的class类型对象
                    Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                    return actualTypeArgument;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * vo 字符串时间 转  LocalDateTime
     *
     * @param classz
     * @param propertyDescriptor
     * @param sourceValue
     * @return
     */
    public static Object convertDateTime(Object classz, PropertyDescriptor propertyDescriptor, Object sourceValue) {
        try {
            Field field = classz.getClass().getDeclaredField(propertyDescriptor.getName());
            // 默认格式
            if (null != field && null != field.getAnnotation(DateTimeFormat.class)) {
                DateTimeFormat dateTimeFormat = field.getAnnotation(DateTimeFormat.class);
                String format = dateTimeFormat.value();
                LocalDateTime parse = LocalDateTime.parse(sourceValue.toString(), DateTimeFormatter.ofPattern(format));
                sourceValue = parse;
                return sourceValue;
            }

        } catch (Exception e) {
        }
        return sourceValue;
    }


    /**
     * entity String  转 vo 对象（
     *
     * @param classz
     * @param propertyDescriptor
     * @param sourceValue
     * @return
     */
    public static Object jsonStr2Obj(Object classz, PropertyDescriptor propertyDescriptor, Object sourceValue) {
        try {
            Field field = classz.getClass().getDeclaredField(propertyDescriptor.getName());
            // 默认格式
            if (null != field && null != field.getAnnotation(JsonStr2Obj.class)) {
                JsonStr2Obj annotation = field.getAnnotation(JsonStr2Obj.class);
                sourceValue = JSON.parseObject(sourceValue.toString(), annotation.value());
                return sourceValue;
            }

        } catch (Exception e) {
        }
        return sourceValue;
    }

    /**
     * list 属性深度拷贝
     *
     * @param classz
     * @param propertyDescriptor
     * @return
     */
    public static boolean isListDepthCopy(Object classz, PropertyDescriptor propertyDescriptor) {
        try {
            Field field = classz.getClass().getDeclaredField(propertyDescriptor.getName());
            // 默认格式
            if (null != field && null != field.getAnnotation(ListDepth.class)) {
                return true;
            }

        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 枚举code 提取
     *
     * @param classz
     * @param propertyDescriptor
     * @param sourceValue
     * @return
     */
//    public static Object value2Enum(Object classz, PropertyDescriptor propertyDescriptor, Object sourceValue) {
//        try {
//            Field field = classz.getClass().getDeclaredField(propertyDescriptor.getName());
//
//
//            Value2Enum annotation = field.getAnnotation(Value2Enum.class);
//            if (null != field && null != annotation) {
//                Class<? extends IEnum> classe = (Class<? extends IEnum>) field.getType();
//                IEnum[] enumConstants = classe.getEnumConstants();
//                for (IEnum enumConstant : enumConstants) {
//                    if (enumConstant.code().equals(sourceValue)) {
//                        return enumConstant;
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//        }
//        return sourceValue;
//    }


    /**
     * Describe: 时间格式 注解
     *
     * @Author xiezhongyong
     * @Date 2019/12/11
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DateTimeFormat {

        String value() default "yyyy-MM-dd HH:mm:ss";
    }

    /**
     * Describe: entity 字符串 json 转对象
     *
     * @Author xiezhongyong
     * @Date 2021-12-10
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface JsonStr2Obj {

        Class value() default JSONObject.class;
    }

    /**
     * list 属性拷贝 深度 下钻 拷贝PS: 该注解使用于 target
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ListDepth {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Value2Enum {
    }

    @FunctionalInterface
    public interface TypeFunctional<T> extends Serializable {
        Object apply(T source);
    }

    public static <T> String getPropertyName(TypeFunctional<T> lambda) {
        try {
            Method method = lambda.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);
            String getterMethod = serializedLambda.getImplMethodName();
            String fieldName = Introspector.decapitalize(getterMethod.replace("get", ""));
            return fieldName;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getIgnorePropertieNames(TypeFunctional... ignoreProperties) {
        List<String> ignorePropertieNames = new ArrayList<>();
        if (ignoreProperties != null && ignoreProperties.length > 0) {
            for (int i = 0; i < ignoreProperties.length; i++) {
                TypeFunctional lambda = ignoreProperties[i];
                //根据lambda表达式得到字段名
                ignorePropertieNames.add(getPropertyName(lambda));
            }
        }

        return ignorePropertieNames;
    }


}
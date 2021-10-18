package com.xforceplus.wapp.common.utils;

import com.xforceplus.wapp.common.dto.FieldBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * 协同产线属性拷贝工具类
 * @author sun shiyong
 */
public class BeanUtil {
  private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

  public static List<String> checkNumberOfDecimalPlaces(Object source) {
    List<String> fields = new ArrayList<>();
    try {
      Field[]  sourceFields = source.getClass().getDeclaredFields();
      for(Field sourceField : sourceFields){
        if (BigDecimal.class.equals(sourceField.getType())) {
          sourceField.setAccessible(true);
          String fieldName = sourceField.getName();
          Method sourceGetMethod=source.getClass().getDeclaredMethod(getGetterOrSetterName(fieldName,"get"));
          sourceGetMethod.setAccessible(true);
          BigDecimal value = (BigDecimal) sourceGetMethod.invoke(source);
          if ("unitPrice".equals(fieldName) || "unitPriceWithTax".equals(fieldName) || "quantity".equals(fieldName) || "originUnitPrice".equals(fieldName)) {
            if (getNumberOfDecimalPlaces(value) > 15) {
              fields.add(fieldName);
            }
          } else if (getNumberOfDecimalPlaces(value) > 2) {
            fields.add(fieldName);
          }
        }
      }
    }catch (Exception e) {
      logger.error("checkNumberOfDecimalPlaces error:", e);
    }
    return fields;
  }

  private static int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
    if (bigDecimal == null) {
      return 0;
    }
    String string = bigDecimal.stripTrailingZeros().toPlainString();
    int index = string.indexOf(".");
    return index < 0 ? 0 : string.length() - index - 1;
  }

  public static Map<String,Object> switchToMap(Object source) {
    Map<String,Object> retMap = new HashMap<>();
    try {
      Field[]  sourceFields = source.getClass().getDeclaredFields();
      for(Field sourceField : sourceFields){
        sourceField.setAccessible(true);
        Method sourceGetMethod=source.getClass().getDeclaredMethod(getGetterOrSetterName(sourceField.getName(),"get"));
        sourceGetMethod.setAccessible(true);
        retMap.put(sourceField.getName(),sourceGetMethod.invoke(source));
      }
    }catch (Exception e){
    logger.error("switchToMap 异常！",e);
    }
    return retMap;
  }

  public static void copyProperties(Object source, Object target) throws BeansException {
    Assert.notNull(source, "Source must not be null");
    Assert.notNull(target, "Target must not be null");
    Set<FieldBean> fieldBeans = new HashSet<>();
    //获取target的所有属性
    Map<String,Class<?>> targetFieldsMap = new HashMap<>(20);
    Field[]  targetFields =target.getClass().getDeclaredFields();
    for(Field targetField : targetFields){
      targetField.setAccessible(true);
      targetFieldsMap.put(targetField.getName(),targetField.getType());
    }
    //获取source的所有属性,如果对应target存在相同属性，则参与拷贝
    Field[]  sourceFields = source.getClass().getDeclaredFields();
    FieldBean fieldBean = null;
    String fieldName = null;
    for(Field sourceField : sourceFields){
      sourceField.setAccessible(true);
      fieldName = sourceField.getName();
      if(fieldName != null && targetFieldsMap.containsKey(fieldName)){
        fieldBean = new FieldBean();
        fieldBean.setFieldName(fieldName);
        fieldBean.setGetterName(getGetterOrSetterName(fieldName,"get"));
        fieldBean.setSetterName(getGetterOrSetterName(fieldName,"set"));
        fieldBean.setGetterType(sourceField.getType());
        fieldBean.setSetterType(targetFieldsMap.get(fieldName));
        fieldBeans.add(fieldBean);
      }
    }

    //执行拷贝
    copyProperties(source,target,fieldBeans);
  }

  public static void copyPropertiesForNull(Object source, Object target,Object dbTarget, Set<FieldBean> fieldBeans) throws BeansException {
    Set<FieldBean> nullFieldBeans = new HashSet<>();
    Iterator<FieldBean> iterator = fieldBeans.iterator();
    FieldBean fieldBean = null;
    while(iterator.hasNext()) {
      fieldBean = iterator.next();
      try {
        Method dbTargetMethod = dbTarget.getClass().getDeclaredMethod(fieldBean.getGetterName());
        Method sourceMethod = source.getClass().getDeclaredMethod(fieldBean.getGetterName());
        dbTargetMethod.setAccessible(true);
        sourceMethod.setAccessible(true);
        if (String.class.equals(fieldBean.getSetterType()) && String.class.equals(fieldBean.getGetterType())){
          //字符型 source不为空，但数据库字段为空，则拷贝
          if (StringUtils.isEmpty(dbTargetMethod.invoke(dbTarget)) && !StringUtils.isEmpty(sourceMethod.invoke(source))){
            nullFieldBeans.add(fieldBean);
            logger.info("参与更新权限的覆盖拷贝，fieldName:{}",fieldBean.getFieldName());
            continue;
          }
        }else if (BigDecimal.class.equals(fieldBean.getSetterType()) && String.class.equals(fieldBean.getGetterType())){
          //金额型
          BigDecimal decimalValue = (BigDecimal)dbTargetMethod.invoke(dbTarget);
          if (decimalValue == null || BigDecimal.ZERO.compareTo(decimalValue) == 0){
            if (!StringUtils.isEmpty(sourceMethod.invoke(source))){
              nullFieldBeans.add(fieldBean);
              logger.info("参与更新权限的覆盖拷贝，fieldName:{}",fieldBean.getFieldName());
              continue;
            }
          }
        }else if (Long.class.equals(fieldBean.getSetterType()) && Long.class.equals(fieldBean.getGetterType())){
          //长数值型
          Long longValue = (Long)dbTargetMethod.invoke(dbTarget);
          if (longValue == null || longValue.compareTo(0L) == 0){
            Long longValueSource = (Long)sourceMethod.invoke(source);
            if (longValueSource != null && longValueSource.compareTo(0L) != 0){
              nullFieldBeans.add(fieldBean);
              logger.info("参与更新权限的覆盖拷贝，fieldName:{}",fieldBean.getFieldName());
              continue;
            }
          }
        }else if (Integer.class.equals(fieldBean.getSetterType()) && Integer.class.equals(fieldBean.getGetterType())){
          //数值型
          Integer integerValue = (Integer)dbTargetMethod.invoke(dbTarget);
          if (integerValue == null || integerValue.compareTo(0) == 0){
            Integer integerValueSource = (Integer)sourceMethod.invoke(source);
            if (integerValueSource != null && integerValueSource.compareTo(0) != 0){
              nullFieldBeans.add(fieldBean);
              logger.info("参与更新权限的覆盖拷贝，fieldName:{}",fieldBean.getFieldName());
              continue;
            }
          }
        }

      }catch (Exception e){
        logger.error("copyPropertiesForNull 异常！",e);
      }
    }

    if (!CollectionUtils.isEmpty(nullFieldBeans)){
      //执行覆盖拷贝
      copyProperties(source,target,nullFieldBeans);
    }
  }
  public static void copyProperties(Object source, Object target, Set<FieldBean> fieldBeans) throws BeansException {
    Iterator<FieldBean> iterator = fieldBeans.iterator();
    while(iterator.hasNext()){
      FieldBean fieldBean = iterator.next();
      try {
        Method targetMethod=target.getClass().getDeclaredMethod(fieldBean.getSetterName(), fieldBean.getSetterType());
        Method sourceMethod=source.getClass().getDeclaredMethod(fieldBean.getGetterName());
        targetMethod.setAccessible(true);
        sourceMethod.setAccessible(true);
        if(fieldBean.getSetterType().equals(fieldBean.getGetterType())) {
          //类型一致，则直接拷贝
          targetMethod.invoke(target, sourceMethod.invoke(source));
        } else if(Date.class.equals(fieldBean.getSetterType()) && String.class.equals(fieldBean.getGetterType())){
          //日期类型  由String转换为Date
          String sourceValue = (String) sourceMethod.invoke(source);
          if(!StringUtils.isEmpty(StringUtils.trimWhitespace(sourceValue))) {
            targetMethod.invoke(target, new Date(Long.parseLong(sourceValue)));
          }
        } else if(Date.class.equals(fieldBean.getSetterType()) && Long.class.equals(fieldBean.getGetterType())){
          //日期类型  由Long转换为Date
          Long sourceValue = (Long) sourceMethod.invoke(source);
          if(sourceValue != null) {
            targetMethod.invoke(target, new Date(sourceValue));
          }
        } else if(BigDecimal.class.equals(fieldBean.getSetterType()) && String.class.equals(fieldBean.getGetterType())){
          //金额类型  由String转换为BigDecimal
          String sourceValue = (String) sourceMethod.invoke(source);
          if(!StringUtils.isEmpty(StringUtils.trimWhitespace(sourceValue))) {
            targetMethod.invoke(target, new BigDecimal(sourceValue));
          }
        } else if(String.class.equals(fieldBean.getSetterType()) && BigDecimal.class.equals(fieldBean.getGetterType())){
          //字符类型  由BigDecimal转换为String
          BigDecimal sourceValue=(BigDecimal) sourceMethod.invoke(source);
          if(sourceValue != null) {
            targetMethod.invoke(target, sourceValue.toPlainString());
          }
        } else if(String.class.equals(fieldBean.getSetterType()) && Date.class.equals(fieldBean.getGetterType())){
          //字符类型  由Date转换为String
          Date sourceValue=(Date) sourceMethod.invoke(source);
          if(sourceValue != null) {
            String dateStr = String.valueOf(sourceValue.getTime());
            if ("14400000".equals(dateStr)){
              //如果是默认日期，则转换为空值
              dateStr = "";
            }
            targetMethod.invoke(target, dateStr);
          }
        } else if(String.class.equals(fieldBean.getSetterType()) && Long.class.equals(fieldBean.getGetterType())){
          //字符类型  由Long转换为String
          Long sourceValue=(Long) sourceMethod.invoke(source);
          if(sourceValue != null) {
            targetMethod.invoke(target, String.valueOf(sourceValue));
          }
        } else if(Long.class.equals(fieldBean.getSetterType()) && String.class.equals(fieldBean.getGetterType())){
          //长整类型  由String转换为Long
          String sourceValue = (String) sourceMethod.invoke(source);
          if(!StringUtils.isEmpty(StringUtils.trimWhitespace(sourceValue))) {
            targetMethod.invoke(target, Long.parseLong(sourceValue));
          }
        } else if(Integer.class.equals(fieldBean.getSetterType()) && String.class.equals(fieldBean.getGetterType())){
          //整数类型  由String转换为Integer
          String sourceValue = (String) sourceMethod.invoke(source);
          if(!StringUtils.isEmpty(StringUtils.trimWhitespace(sourceValue))) {
            targetMethod.invoke(target, Integer.parseInt(sourceValue));
          }
        } else if(Long.class.equals(fieldBean.getSetterType()) && Date.class.equals(fieldBean.getGetterType())){
          //长整类型  由Date转换为Long
          Date sourceValue=(Date) sourceMethod.invoke(source);
          if(sourceValue != null) {
            targetMethod.invoke(target, sourceValue.getTime());
          }
        }
      }catch (NoSuchMethodException nsme){
        //如没有该方法，则不执行拷贝
        continue;
      }catch (NumberFormatException nfe){
        //数值格式异常，则不执行拷贝
        continue;
      }catch(Throwable th) {
        throw new FatalBeanException("Could not copy property '" + fieldBean.getFieldName() + "' from source to target", th);
      }
    }
  }

  public static String getGetterOrSetterName(String fieldName,String prefix){
    String first=fieldName.substring(0, 1);
    String last=fieldName.substring(1);
    return prefix+first.toUpperCase()+last;
  }

  /**
   * 获取对象属性名列表
   * @param source
   * @return
   */
  public static List<String> getFieldNameList(Class source){
    List<String> fieldList=new ArrayList<>();
    Field[]  rowFields =source.getDeclaredFields();
    for(Field rowField : rowFields){
      rowField.setAccessible(true);
      fieldList.add(rowField.getName());
    }
    return fieldList;
  }

  public static String getObjectValue(Object source,String fieldName){
    String result = "";
    String getMethodName = getGetterOrSetterName(fieldName,"get");
    try {
      Method sourceMethod = null;
      try{
        sourceMethod=source.getClass().getDeclaredMethod(getMethodName);
      }catch (NoSuchMethodException nsme){
        Class clazz = source.getClass();
        sourceMethod=clazz.getSuperclass().getDeclaredMethod(getMethodName);
      }
      sourceMethod.setAccessible(true);

      if(Date.class.equals(sourceMethod.getReturnType())){
        //Date转String
        Date sourceValue=(Date) sourceMethod.invoke(source);
        if(sourceValue != null) {
          result = String.valueOf(sourceValue.getTime());
        }
      }else if (BigDecimal.class.equals(sourceMethod.getReturnType())){
        //BigDecimal转换为String
        BigDecimal sourceValue=(BigDecimal) sourceMethod.invoke(source);
        if(sourceValue != null) {
          result = sourceValue.toPlainString();
        }
      }else if (Long.class.equals(sourceMethod.getReturnType())){
        //Long转换为String
        Long sourceValue=(Long) sourceMethod.invoke(source);
        if(sourceValue != null) {
          result = String.valueOf(sourceValue);
        }
      }else if (Integer.class.equals(sourceMethod.getReturnType())){
        //Integer转换为String
        Integer sourceValue=(Integer) sourceMethod.invoke(source);
        if(sourceValue != null) {
          result = String.valueOf(sourceValue);
        }
      }else if (String.class.equals(sourceMethod.getReturnType())){
        result = (String) sourceMethod.invoke(source);
      }
    } catch (Exception e) {
      logger.error("getObjectValue exception:",e);
    }
    return result;
  }

  public static void setObjectValueSpecialDecimal(Object target,String fieldValue,String fieldKey){
    String setMethodName = getGetterOrSetterName(fieldKey,"set");
    String getMethodName = getGetterOrSetterName(fieldKey,"get");
    try {
      Method targetMethod = null;
      Class fieldType = null;
      try{
        fieldType=target.getClass().getDeclaredMethod(getMethodName).getReturnType();
        targetMethod=target.getClass().getDeclaredMethod(setMethodName,fieldType);
      }catch (NoSuchMethodException nsme){
        Class clazz = target.getClass();
        fieldType=clazz.getSuperclass().getDeclaredMethod(getMethodName).getReturnType();
        targetMethod=clazz.getSuperclass().getDeclaredMethod(setMethodName,fieldType);
      }
      targetMethod.setAccessible(true);
      if(Date.class.equals(fieldType)){
        //日期类型  由String转换为Date
        targetMethod.invoke(target, new Date(Long.parseLong(fieldValue)));
      }else if (BigDecimal.class.equals(fieldType)){
        //金额类型  由String转换为BigDecimal
        if ("unitPrice".equals(fieldKey) || "unitPriceWithTax".equals(fieldKey) || "originUnitPrice".equals(fieldKey)) {
          // 单价类 保留15位小数
          targetMethod.invoke(target, new BigDecimal(fieldValue).setScale(15, BigDecimal.ROUND_HALF_UP));
        } else if ("quantity".equals(fieldKey)) {
          // 数量保留6位小数
          targetMethod.invoke(target, new BigDecimal(fieldValue).setScale(6, BigDecimal.ROUND_HALF_UP));
        } else {
          // 其他金额保留2位小数
          targetMethod.invoke(target, new BigDecimal(fieldValue).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
      }else if (Long.class.equals(fieldType)){
        //长整类型  由String转换为Long
        targetMethod.invoke(target, Long.parseLong(fieldValue));
      }else if (Integer.class.equals(fieldType)){
        //整数类型  由String转换为Integer
        targetMethod.invoke(target, Integer.parseInt(fieldValue));
      }else if (String.class.equals(fieldType)){
        targetMethod.invoke(target, fieldValue);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void setObjectValue(Object target,String fieldValue,String fieldKey){
    String setMethodName = getGetterOrSetterName(fieldKey,"set");
    String getMethodName = getGetterOrSetterName(fieldKey,"get");
    try {
      Method targetMethod = null;
      Class fieldType = null;
      try{
        fieldType=target.getClass().getDeclaredMethod(getMethodName).getReturnType();
        targetMethod=target.getClass().getDeclaredMethod(setMethodName,fieldType);
      }catch (NoSuchMethodException nsme){
        Class clazz = target.getClass();
        fieldType=clazz.getSuperclass().getDeclaredMethod(getMethodName).getReturnType();
        targetMethod=clazz.getSuperclass().getDeclaredMethod(setMethodName,fieldType);
      }
      targetMethod.setAccessible(true);
      if(Date.class.equals(fieldType)){
        //日期类型  由String转换为Date
        targetMethod.invoke(target, new Date(Long.parseLong(fieldValue)));
      }else if (BigDecimal.class.equals(fieldType)){
        //金额类型  由String转换为BigDecimal
        targetMethod.invoke(target, new BigDecimal(fieldValue));
      }else if (Long.class.equals(fieldType)){
        //长整类型  由String转换为Long
        targetMethod.invoke(target, Long.parseLong(fieldValue));
      }else if (Integer.class.equals(fieldType)){
        //整数类型  由String转换为Integer
        targetMethod.invoke(target, Integer.parseInt(fieldValue));
      }else if (String.class.equals(fieldType)){
        targetMethod.invoke(target, fieldValue);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 拷贝源List<Object> 到 目标 List<Object>
   * @param sourceList
   * @param targetList
   * @param targetClazz
   */
  public static <T,S> void copyList(List<T> sourceList,List<S> targetList,Class<S> targetClazz){
    if (! CollectionUtils.isEmpty(sourceList)){
      S targetObject = null;
      for (Object sourceObject : sourceList ){
        try {
          targetObject = targetClazz.newInstance();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        copyProperties(sourceObject,targetObject);
        targetList.add(targetObject);
      }
    }
  }

  public static boolean equalsByFieldType(Object target, String fieldKey, String fieldValue) {
    if (fieldValue == null) {
      return false;
    }
    String getMethodName = getGetterOrSetterName(fieldKey,"get");
    try {
      Method sourceMethod = null;
      try {
        sourceMethod = target.getClass().getDeclaredMethod(getMethodName);
      } catch (NoSuchMethodException nsme) {
        Class clazz = target.getClass();
        sourceMethod = clazz.getSuperclass().getDeclaredMethod(getMethodName);
      }
      sourceMethod.setAccessible(true);

      if (Date.class.equals(sourceMethod.getReturnType())) {
        //Date转String
        Date sourceValue = (Date) sourceMethod.invoke(target);
        if (sourceValue != null) {
          return sourceValue.getTime()== Long.parseLong(fieldValue);
        }
      } else if (BigDecimal.class.equals(sourceMethod.getReturnType())) {
        //BigDecimal转换为String
        BigDecimal sourceValue = (BigDecimal) sourceMethod.invoke(target);
        if (sourceValue != null) {
          return sourceValue.compareTo(new BigDecimal(fieldValue)) == 0;
        }
      } else if (Long.class.equals(sourceMethod.getReturnType())) {
        //Long转换为String
        Long sourceValue = (Long) sourceMethod.invoke(target);
        if (sourceValue != null) {
          return sourceValue.equals(Long.valueOf(fieldValue));
        }
      } else if (Integer.class.equals(sourceMethod.getReturnType())) {
        //Integer转换为String
        Integer sourceValue = (Integer) sourceMethod.invoke(target);
        if (sourceValue != null) {
          return sourceValue.equals(Integer.valueOf(fieldValue));
        }
      } else {
        String sourceValue = (String) sourceMethod.invoke(target);
        return Objects.equals(fieldValue, sourceValue);
      }
    } catch (Exception e) {
      logger.error("getObjectValue exception:", e);
    }
    return false;
  }

}

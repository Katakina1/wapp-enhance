package com.xforceplus.wapp.common.dto;

import java.util.Objects;

/**
 * 协同产线字段信息
 * @author sun shiyong
 */
public class FieldBean {
  private String fieldName;
  private String getterName;
  private String setterName;
  private Class<?> getterType;
  private Class<?> setterType;

  public FieldBean(String fieldName, String getterName, String setterName, Class<?> getterType, Class<?> setterType){
    this.fieldName = fieldName;
    this.getterName = getterName;
    this.setterName = setterName;
    this.getterType = getterType;
    this.setterType = setterType;
  }
  public FieldBean(){

  }

  @Override
  public boolean equals(Object o) {
    if (this == o){return true;}
    if (o == null || getClass() != o.getClass()) {return false;}
    FieldBean that = (FieldBean) o;
    return Objects.equals(fieldName, that.fieldName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldName);
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getGetterName() {
    return getterName;
  }

  public void setGetterName(String getterName) {
    this.getterName = getterName;
  }

  public String getSetterName() {
    return setterName;
  }

  public void setSetterName(String setterName) {
    this.setterName = setterName;
  }

  public Class<?> getGetterType() {
    return getterType;
  }

  public void setGetterType(Class<?> getterType) {
    this.getterType = getterType;
  }

  public Class<?> getSetterType() {
    return setterType;
  }

  public void setSetterType(Class<?> setterType) {
    this.setterType = setterType;
  }
}

package com.facialrecognition.annotation;
/*
 * 定义注解
 * */

import com.facialrecognition.entity.enums.PermissionCodeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalInterceptor {
    boolean checkLogin() default true;
    PermissionCodeEnum permissionCode() default PermissionCodeEnum.NO_PERMISSION;
    boolean checkParams() default true;
}

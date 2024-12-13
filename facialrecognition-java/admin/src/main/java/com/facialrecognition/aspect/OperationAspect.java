package com.facialrecognition.aspect;

import com.facialrecognition.annotation.GlobalInterceptor;
import com.facialrecognition.annotation.VerifyParam;
import com.facialrecognition.entity.constants.Constants;
import com.facialrecognition.entity.dto.SessionUserAdminDto;
import com.facialrecognition.entity.enums.PermissionCodeEnum;
import com.facialrecognition.entity.enums.ResponseCodeEnum;
import com.facialrecognition.exception.BusinessException;
import com.facialrecognition.utils.StringTools;
import com.facialrecognition.utils.VerifyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author 吴磊
 * @version 1.0
 * @description: AOP切面类
 * @date 2024/11/27 22:17
 */
@Aspect
@Component("OperationAspect")
public class OperationAspect {
/*    @Pointcut("@annotation(com.facialrecognition.annotation.GlobalInterceptor)")
    private void pointcut(){

    }*/
    private static final String[] BASE_TYPE_ARRAY = new String[]{"java.lang.String", "java.lang.Integer", "java.lang.Long"};

    private Logger logger = LoggerFactory.getLogger(OperationAspect.class);
    /**
     * @description:  切点 拦截所有有注解的方法
     * @param: null
     * @return:
     * @author 吴磊
     * @date: 2024/11/27 23:42
     */
    @Before("@annotation(com.facialrecognition.annotation.GlobalInterceptor)")
    public void interceptorDo(JoinPoint point){
        //logger.info(point.getArgs().toString());
        Object[] arguments = point.getArgs();
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
        if (interceptor == null) {
            return;
        }
        /**
         * 登录校验
         */
        if(interceptor.checkLogin()){
            checkLogin();
        }

        /**
         * 校验权限
         **/
        if(interceptor.permissionCode() != null && interceptor.permissionCode() != PermissionCodeEnum.NO_PERMISSION){
            checkPermission(interceptor.permissionCode());
        }

        /*校验参数*/
        if (interceptor.checkParams()) {
            validateParams(method, arguments);
        }

    }

    void checkLogin(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        SessionUserAdminDto sessionUserAdminDto = (SessionUserAdminDto) request.getSession().getAttribute(Constants.SESSION_KEY);
        if (sessionUserAdminDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
    }
    void  checkPermission(PermissionCodeEnum permissionCodeEnum){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        SessionUserAdminDto sessionUserAdminDto = (SessionUserAdminDto) request.getSession().getAttribute(Constants.SESSION_KEY);
        List<String> permissionsCodeList = sessionUserAdminDto.getPermissionCodeList();
        if(!permissionsCodeList.contains(permissionCodeEnum.getCode())){
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
    }
    private void checkObjValue(Parameter parameter, Object value) {
        try {
            String typeName = parameter.getParameterizedType().getTypeName();
            Class classz = Class.forName(typeName);
            Field[] fields = classz.getDeclaredFields();
            for (Field field : fields) {
                VerifyParam fieldVerifyParam = field.getAnnotation(VerifyParam.class);
                if (fieldVerifyParam == null) {
                    continue;
                }
                field.setAccessible(true);
                Object resultValue = field.get(value);
                checkValue(resultValue, fieldVerifyParam);
            }

        } catch (Exception e) {
            logger.error("校验参数失败", e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
    /*参数校验*/
    private void validateParams(Method method, Object[] arguments) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = arguments[i];
            VerifyParam verifyParam = parameter.getAnnotation(VerifyParam.class);
            if (verifyParam == null) {
                continue;
            }
            String paramTypeName = parameter.getParameterizedType().getTypeName();
            /**
             * 基本数据类型
             */
            if (ArrayUtils.contains(BASE_TYPE_ARRAY, paramTypeName)) {
                checkValue(value, verifyParam);
            } else {
                checkObjValue(parameter, value);
            }
        }
    }
    private void checkValue(Object value, VerifyParam verifyParam) {
        Boolean isEmpty = value == null || StringTools.isEmpty(value.toString());
        Integer length = value == null ? 0 : value.toString().length();

        /**
         * 校验空
         */
        if (isEmpty && verifyParam.required()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        /**
         * 校验长度
         */
        if (!isEmpty && (verifyParam.max() != -1 && verifyParam.max() < length || verifyParam.min() != -1 && verifyParam.min() > length)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        /**
         * 校验正则
         */
        if (!isEmpty && !StringTools.isEmpty(verifyParam.regex().getRegex()) && !VerifyUtils.verify(verifyParam.regex(), String.valueOf(value))) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

}

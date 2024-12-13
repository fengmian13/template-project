package com.facialrecognition.controller;

import com.facialrecognition.annotation.GlobalInterceptor;
import com.facialrecognition.annotation.VerifyParam;
import com.facialrecognition.entity.constants.Constants;
import com.facialrecognition.entity.dto.CreateImageCode;
import com.facialrecognition.entity.dto.SessionUserAdminDto;
import com.facialrecognition.entity.enums.ResponseCodeEnum;
import com.facialrecognition.entity.enums.SysAccountStatusEnum;
import com.facialrecognition.entity.enums.VerifyRegexEnum;
import com.facialrecognition.entity.po.SysAccount;
import com.facialrecognition.entity.vo.ResponseVO;
import com.facialrecognition.exception.BusinessException;
import com.facialrecognition.service.SysAccountService;
import com.facialrecognition.utils.StringTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@RestController
public class loginController extends ABaseController{

    @Resource
    private SysAccountService sysAccountService;

    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();
        session.setAttribute(Constants.CHECK_CODE_KEY, code);
        vCode.write(response.getOutputStream());
    }
   

    @RequestMapping("/login")
    @GlobalInterceptor(checkLogin = false)
    public ResponseVO login(HttpSession session,
                            @VerifyParam(regex = VerifyRegexEnum.PHONE) String phone,
                            @VerifyParam(required = true) String password,
                            @VerifyParam(required = true) String checkCode) {
        if (!checkCode.equals((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
            throw new BusinessException("验证码错误");
        }
        SessionUserAdminDto userAdminDto =  sysAccountService.login(phone,password);
        session.setAttribute(Constants.SESSION_KEY,userAdminDto);
        return getSuccessResponseVO(userAdminDto);
    }

/*    @RequestMapping("/login")
    @GlobalInterceptor(checkLogin = false)
    public ResponseVO login(HttpSession session,
                            @VerifyParam(required = true) SysAccount sysAccount,
                            @VerifyParam(required = true) String checkCode) {

        if (!checkCode.equals((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
            throw new BusinessException("验证码错误");
        }
        sysAccountService.login(sysAccount.getPhone(),sysAccount.getPassword());
        return getSuccessResponseVO(null);
    }*/
@RequestMapping("/logout")
@GlobalInterceptor(checkLogin = false)
public ResponseVO logout(HttpSession session) {
    session.invalidate();
    return getSuccessResponseVO(null);
}


    @RequestMapping("/updateMyPwd")
    @GlobalInterceptor
    public ResponseVO updateMyPwd(HttpSession session,
                                  @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD) String password) {
        SessionUserAdminDto userAdminDto = getUserAdminFromSession(session);
        SysAccount sysAccount = new SysAccount();
        sysAccount.setPassword(StringTools.encodeByMD5(password));
        sysAccountService.updateSysAccountByUserId(sysAccount, userAdminDto.getUserId());
        return getSuccessResponseVO(null);
    }
    

}

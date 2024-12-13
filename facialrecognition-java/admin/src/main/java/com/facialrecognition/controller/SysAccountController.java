package com.facialrecognition.controller;

import java.util.List;

import com.facialrecognition.annotation.GlobalInterceptor;
import com.facialrecognition.annotation.VerifyParam;
import com.facialrecognition.entity.config.AppConfig;
import com.facialrecognition.entity.enums.PermissionCodeEnum;
import com.facialrecognition.entity.enums.ResponseCodeEnum;
import com.facialrecognition.entity.enums.UserStatusEnum;
import com.facialrecognition.entity.enums.VerifyRegexEnum;
import com.facialrecognition.entity.query.SysAccountQuery;
import com.facialrecognition.entity.po.SysAccount;
import com.facialrecognition.entity.vo.ResponseVO;
import com.facialrecognition.exception.BusinessException;
import com.facialrecognition.service.SysAccountService;
import com.facialrecognition.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 账号信息 Controller
 */
@RestController("sysAccountController")
@RequestMapping("/settings")
public class SysAccountController extends com.facialrecognition.controller.ABaseController {

	@Resource
	private SysAccountService sysAccountService;

	@Resource
	private AppConfig appConfig;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadAccountList")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ACCOUNT_LIST)
	public ResponseVO loadDataList(SysAccountQuery query){
		query.setOrderBy("create_time desc");
		query.setQueryRoles(true);
		return getSuccessResponseVO(sysAccountService.findListByPage(query));
	}

	@RequestMapping("/saveAccount")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ACCOUNT_EDIT)
	public ResponseVO saveAccount(@VerifyParam(required = true)SysAccount sysAccount){
		sysAccountService.saveSysAccount(sysAccount);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/updatePassword")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ACCOUNT_UPDATE_PASSWORD)
	public ResponseVO updatePassword(@VerifyParam Integer userId,
									 @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD) String password) {
		SysAccount updateInfo = new SysAccount();
		updateInfo.setPassword(StringTools.encodeByMD5(password));
		sysAccountService.updateSysAccountByUserId(updateInfo, userId);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/updateStatus")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ACCOUNT_OP_STATUS)
	public ResponseVO updateStatus(@VerifyParam Integer userId,
									 @VerifyParam(required = true) Integer status) {
		UserStatusEnum userStatusEnum = UserStatusEnum.getByStatus(status);
		if (userStatusEnum == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		SysAccount updateInfo = new SysAccount();
		updateInfo.setStatus(status);
		sysAccountService.updateSysAccountByUserId(updateInfo, userId);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/delAccount")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ACCOUNT_DEL)
	public ResponseVO delAccount(@VerifyParam Integer userId) {
		SysAccount sysAccount = this.sysAccountService.getSysAccountByUserId(userId);
		if (!StringTools.isEmpty(appConfig.getSuperAdminPhones()) && ArrayUtils.contains(appConfig.getSuperAdminPhones().split(","), sysAccount.getPhone())) {
			throw new BusinessException("系统超级管理员不允许删除");
		}
		/*该账号正在被使用不允许删除*/


		sysAccountService.deleteSysAccountByUserId(userId);
		return getSuccessResponseVO(null);
	}

}
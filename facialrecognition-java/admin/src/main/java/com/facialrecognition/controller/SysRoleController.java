package com.facialrecognition.controller;

import java.util.List;

import com.facialrecognition.annotation.GlobalInterceptor;
import com.facialrecognition.annotation.VerifyParam;
import com.facialrecognition.entity.enums.PermissionCodeEnum;
import com.facialrecognition.entity.query.SysRoleQuery;
import com.facialrecognition.entity.po.SysRole;
import com.facialrecognition.entity.vo.ResponseVO;
import com.facialrecognition.service.SysRoleService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 系统角色表 Controller
 */
@RestController("sysRoleController")
@RequestMapping("/settings")
public class SysRoleController extends ABaseController{

	@Resource
	private SysRoleService sysRoleService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadRoles")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ROLE_LIST)
	public ResponseVO loadRoles(SysRoleQuery query){
		query.setOrderBy("create_time desc");
		return getSuccessResponseVO(sysRoleService.findListByPage(query));
	}
	/**
	 * 根据获取角色索引
	 */
	@RequestMapping("/loadAllRoles")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ROLE_LIST)
	public ResponseVO loadAllRoles() {
		SysRoleQuery query = new SysRoleQuery();
		query.setOrderBy("create_time desc");
		return getSuccessResponseVO(sysRoleService.findListByParam(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/saveRole")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ROLE_EDIT)
	public ResponseVO add(@VerifyParam SysRole bean,
						  String menuIds,
						  String halfMenuIds) {
		sysRoleService.saveRole(bean, menuIds, halfMenuIds);
		return getSuccessResponseVO(null);
	}

	/**
	 * 单独角色菜单
	 */
	@RequestMapping("/saveRoleMenu")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ROLE_EDIT)
	public ResponseVO saveRoleMenu(@VerifyParam(required = true) Integer roleId,
								   @VerifyParam(required = true) String menuIds,
								   String halfMenuIds) {
		sysRoleService.saveRoleMenu(roleId, menuIds, halfMenuIds);
		return getSuccessResponseVO(null);
	}

	/**
	 * 获取角色详情
	 */
	@RequestMapping("/getRoleByRoleId")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ROLE_DEL)
	public ResponseVO getRoleByRoleId(@VerifyParam Integer roleId) {
		return getSuccessResponseVO(sysRoleService.getSysRoleByRoleId(roleId));
	}

	@RequestMapping("/delRole")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_ROLE_DEL)
	public ResponseVO delRole(@VerifyParam(required = true) Integer roleId) {
		sysRoleService.deleteSysRoleByRoleId(roleId);
		return getSuccessResponseVO(null);
	}



}
package com.facialrecognition.controller;

import java.util.List;

import com.facialrecognition.annotation.GlobalInterceptor;
import com.facialrecognition.annotation.VerifyParam;
import com.facialrecognition.entity.enums.PermissionCodeEnum;
import com.facialrecognition.entity.query.SysMenuQuery;
import com.facialrecognition.entity.po.SysMenu;
import com.facialrecognition.entity.vo.ResponseVO;
import com.facialrecognition.service.SysMenuService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 菜单表 Controller
 */
@RestController("sysMenuController")
@RequestMapping("/settings")
public class SysMenuController extends ABaseController{

	@Resource
	private SysMenuService sysMenuService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/menuList")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_MENU)
	public ResponseVO menuList(){
		SysMenuQuery query = new SysMenuQuery();
		query.setFormate2Tree(true);
		query.setOrderBy("sort asc");
		List<SysMenu> sysMenuList = sysMenuService.findListByParam(query);
		return getSuccessResponseVO(sysMenuList);
	}
/**
 * @description:  新增菜单
 * @param: null
 * @return:  null
 * @author 吴磊
 * @date: 2024/11/28 18:53
 */

	@RequestMapping("/saveMenu")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_MENU_EDIT)
	public ResponseVO saveMenu(@VerifyParam SysMenu sysMenu) {
		sysMenuService.saveMenu(sysMenu);
		return getSuccessResponseVO(null);
	}
/**
 * @description:  删除菜单
 * @param: null
 * @return:
 * @author 吴磊
 * @date: 2024/11/28 18:55
 */

	@RequestMapping("/delMenu")
	@GlobalInterceptor(permissionCode = PermissionCodeEnum.SETTINGS_MENU_EDIT)
	public ResponseVO delMenu(@VerifyParam(required = true) Integer menuId) {
		sysMenuService.deleteSysMenuByMenuId(menuId);
		return getSuccessResponseVO(null);
	}
}
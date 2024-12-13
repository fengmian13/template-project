package com.facialrecognition.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.facialrecognition.entity.enums.MenuCheckTypeEnum;
import com.facialrecognition.entity.enums.ResponseCodeEnum;
import com.facialrecognition.entity.po.SysAccount;
import com.facialrecognition.entity.po.SysRole2Menu;
import com.facialrecognition.entity.query.SysAccountQuery;
import com.facialrecognition.entity.query.SysRole2MenuQuery;
import com.facialrecognition.exception.BusinessException;
import com.facialrecognition.mappers.SysAccountMapper;
import com.facialrecognition.mappers.SysRole2MenuMapper;
import org.springframework.stereotype.Service;

import com.facialrecognition.entity.enums.PageSize;
import com.facialrecognition.entity.query.SysRoleQuery;
import com.facialrecognition.entity.po.SysRole;
import com.facialrecognition.entity.vo.PaginationResultVO;
import com.facialrecognition.entity.query.SimplePage;
import com.facialrecognition.mappers.SysRoleMapper;
import com.facialrecognition.service.SysRoleService;
import com.facialrecognition.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 系统角色表 业务接口实现
 */
@Service("sysRoleService")
public class SysRoleServiceImpl implements SysRoleService {

	@Resource
	private SysRoleMapper<SysRole, SysRoleQuery> sysRoleMapper;

	@Resource
	private SysRole2MenuMapper<SysRole2Menu, SysRole2MenuQuery> sysRole2MenuMapper;
	@Resource
	private SysAccountMapper<SysAccount, SysAccountQuery> sysAccountMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<SysRole> findListByParam(SysRoleQuery param) {
		return this.sysRoleMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(SysRoleQuery param) {
		return this.sysRoleMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<SysRole> findListByPage(SysRoleQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<SysRole> list = this.findListByParam(param);
		PaginationResultVO<SysRole> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(SysRole bean) {
		return this.sysRoleMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<SysRole> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.sysRoleMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<SysRole> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.sysRoleMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(SysRole bean, SysRoleQuery param) {
		StringTools.checkParam(param);
		return this.sysRoleMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(SysRoleQuery param) {
		StringTools.checkParam(param);
		return this.sysRoleMapper.deleteByParam(param);
	}

	/**
	 * 根据RoleId获取对象
	 */
	@Override
	public SysRole getSysRoleByRoleId(Integer roleId) {
		SysRole sysRole = this.sysRoleMapper.selectByRoleId(roleId);
		List<Integer> selectedMenuIds = this.sysRoleMapper.selectMenuIdsByRoleIds(new String[]{String.valueOf(roleId)});
		sysRole.setMenuIds(selectedMenuIds);
		return sysRole;
	}

	/**
	 * 根据RoleId修改
	 */
	@Override
	public Integer updateSysRoleByRoleId(SysRole bean, Integer roleId) {
		return this.sysRoleMapper.updateByRoleId(bean, roleId);
	}

	/**
	 * 根据RoleId删除
	 */
	@Override
	public Integer deleteSysRoleByRoleId(Integer roleId) {
		SysAccountQuery query = new SysAccountQuery();
		query.setRoles(String.valueOf(roleId));
		Integer count = sysAccountMapper.selectCount(query);
		if (count > 0){
			throw new BusinessException("该角色正在被使用，不能删除");
		}
		count = this.sysRoleMapper.deleteByRoleId(roleId);

		SysRole2MenuQuery role2MenuQuery = new SysRole2MenuQuery();
		role2MenuQuery.setRoleId(roleId);
		sysRole2MenuMapper.deleteByParam(role2MenuQuery);

		return count;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)//事务回滚
	public void saveRole(SysRole sysRole, String menuIds, String halfMenuIds) {
		Integer roleId = sysRole.getRoleId();
		Boolean addMenu = false;
		if (null == roleId){
			Date currentDate = new Date();
			sysRole.setCreateTime(currentDate);
			sysRole.setLastUpdateTime(currentDate);
			this.sysRoleMapper.insert(sysRole);
			roleId = sysRole.getRoleId();
			addMenu = true;
		}else {
			sysRole.setCreateTime(null);
			this.sysRoleMapper.updateByRoleId(sysRole, roleId);
		}
		SysRoleQuery sysRoleQuery = new SysRoleQuery();
		sysRoleQuery.setRoleName(sysRole.getRoleName());
		Integer roleCount = this.sysRoleMapper.selectCount(sysRoleQuery);
		if(roleCount > 1){
			throw new BusinessException("角色名称重复");
		}
		if (addMenu){
			saveRoleMenu(roleId, menuIds, halfMenuIds);
		}
	}


	@Transactional(rollbackFor = Exception.class)//事务回滚
	public void saveRoleMenu(Integer roleId, String menuIds, String halfMenuIds) {
		if(null == roleId || null == menuIds){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		List<SysRole2Menu> role2MenuList = new ArrayList<>();
		SysRole2MenuQuery role2MenuQuery = new SysRole2MenuQuery();
		role2MenuQuery.setRoleId(roleId);
		sysRole2MenuMapper.deleteByParam(role2MenuQuery);
		String[] menuIdArray = menuIds.split(",");
		String[] halfMenuIdArray =StringTools.isEmpty(halfMenuIds) ? new String[]{} : halfMenuIds.split(",");

		convertMenuId2RoleMenu(role2MenuList, roleId, menuIdArray, MenuCheckTypeEnum.ALL);
		convertMenuId2RoleMenu(role2MenuList, roleId, halfMenuIdArray, MenuCheckTypeEnum.HALF);

		if (!role2MenuList.isEmpty()){
			sysRole2MenuMapper.insertBatch(role2MenuList);
		}
	}


	private void convertMenuId2RoleMenu(List<SysRole2Menu> role2MenuList, Integer roleId, String[] menuIdArray,
										MenuCheckTypeEnum checkTypeEnum){
		for(String menuId : menuIdArray){
			SysRole2Menu role2Menu = new SysRole2Menu();
			role2Menu.setMenuId(Integer.parseInt(menuId));
			role2Menu.setRoleId(roleId);
			role2Menu.setCheckType(checkTypeEnum.getType());
			role2MenuList.add(role2Menu);
		}
	}
}
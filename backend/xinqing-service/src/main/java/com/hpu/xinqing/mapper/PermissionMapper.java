package com.hpu.xinqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.entity.Permission;
import com.hpu.xinqingpojo.entity.RolePermission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {
    List<String> getPermissionByPermissionIds(List<RolePermission> rolePermissions);
}

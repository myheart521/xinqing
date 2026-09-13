package com.hpu.xinqing.config;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.mapper.PermissionMapper;
import com.hpu.xinqing.mapper.RoleMapper;
import com.hpu.xinqing.mapper.RolePermissionMapper;
import com.hpu.xinqing.mapper.UserMapper;
import com.hpu.xinqingpojo.entity.Role;
import com.hpu.xinqingpojo.entity.RolePermission;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 根据业务逻辑查询权限
        User user =  userMapper.selectById((Long) loginId);
        Integer roleId = user.getRoleId();
        QueryWrapper<RolePermission> role_id = new QueryWrapper<RolePermission>().ge("role_id", roleId);
        //获取全部权限id
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(role_id);
        List<String> permissions = permissionMapper.getPermissionByPermissionIds(rolePermissions);
        return permissions;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        User user = userMapper.selectById(Integer.parseInt(loginId.toString()));
        if(user==null){
            log.error("登陆的id为"+loginId);
        }
        Integer roleId = user.getRoleId();
        Role role = roleMapper.selectById(roleId);
        List<String> roleList = new ArrayList<>();
        roleList.add(role.getRoleName());
        return roleList;
    }

}

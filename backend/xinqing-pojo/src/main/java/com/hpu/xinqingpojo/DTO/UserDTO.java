package com.hpu.xinqingpojo.DTO;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户角色：user/admin/ban
     */
    private Integer roleId;
}

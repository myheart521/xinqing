package com.hpu.xinqingpojo.DTO;

import lombok.Data;

@Data
public class ModifyUserDTO {
    private String email;
    private String password;
    private String checkPassword;
    private String userName;
    private String userAvatar;
    private String userProfile;
    private String userRole;
    private String phone;
    private String schoolName;
    private String province;
    private String sex;
}

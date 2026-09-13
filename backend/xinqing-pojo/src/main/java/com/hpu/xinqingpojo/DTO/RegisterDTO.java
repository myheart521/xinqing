package com.hpu.xinqingpojo.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hpu.xinqingpojo.enmus.RoleEnums;
import lombok.Builder;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterDTO {
    private String password;
    private String account;
    private String checkPassword;
    private String userName;
    private String userAvatar="/assets/placeholder.svg";
    private String email;
    private String code;
    private String province;
    private String school;
    private RoleEnums roleId;
    private String registerCode;
    private Long teacherId;


}

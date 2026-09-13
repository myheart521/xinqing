package com.hpu.xinqingpojo.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserInfoDTO {
    /**
     * 用户头像
     */
    @NotBlank(message = "用户头像不能为空")
    private String userAvatar;

    @NotBlank(message = "用户的身份不能为空")
    private Integer roleId;
    /**
     * 用户简介
     */
    @NotBlank(message = "用户简介不能为空")
    private String userProfile;

    @NotBlank(message = "用户的背景图至少得来一个吧")
    private String avatars;
    /**
     * 用户手机号
     */
    @NotBlank(message = "用户手机号不能为空")
    private String phone;

    @NotBlank(message = "用户性别不能为空")
    private String sex;
    //省份
    @NotBlank(message = "用户省份不能为空")
    private String province;

    //学院
    @NotBlank(message = "用户学院不能为空")
    private String college;

    //专业及班级
    @NotBlank(message = "用户专业及班级不能为空")
    private String majorClass;

    @NotBlank(message = "用户学号不能为空")
    private String studentNumber;

    @NotBlank(message="真实姓名不能为空")
    private String userName;
}



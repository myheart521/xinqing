package com.hpu.xinqingpojo.VO;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;
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
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin/ban
     */
    private Integer roleId;
    /**
     * 学校
     */
    private String school;
    /**
     * 学号
     */
    private String studentNumber;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户手机号
     */
    private String phone;
    //性别
    private String sex;
    //省份
    private String province;
    //背景图
    private String avatars;
    //学院
    private String college;
    //专业班级，eg：软件2203
    private String majorClass;
}

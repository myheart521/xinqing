package com.hpu.xinqing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.DTO.*;
import com.hpu.xinqingpojo.VO.BlogVO;
import com.hpu.xinqingpojo.VO.LoginVO;
import com.hpu.xinqingpojo.VO.UserCount;
import com.hpu.xinqingpojo.VO.UserVO;
import com.hpu.xinqingpojo.enmus.RoleEnums;
import com.hpu.xinqingpojo.entity.Province;
import com.hpu.xinqingpojo.entity.Universities;
import com.hpu.xinqingpojo.entity.User;

import java.util.List;
import java.util.Map;


public interface UserService extends IService<User> {
    void register(RegisterDTO registerDTO);

    LoginVO login(LoginDTO loginDTO);

    LoginVO accountLogin(AccountLoginDTO accountLoginDTO);

    List<Province> getAllProvince();

    List<Universities> getSchoolByProvinceId(ShoolByProvinceDTO shoolByProvinceDTO);

    void checkEmail(String email);

    void feedback(FeedbackDTO feedbackDTO);

    void modifyUser(ModifyUserDTO modifyUserDTO);

    LoginVO emailLogin(EmailLoginDTO emailLoginDTO);

    /**
     * 获取用户的发表的动态中的数据，最近的三条
     */
    List<BlogAnalyseDTO> getBlogByUserId(Long userId);

    /**
     * 获取用户的发表的活动，最近的三条
     */
    List<ActiveAnalyseDTO> getActivityByUserId(Long userId);

    /**
     * 获取用户关注的人发的博客，最近三条
     */
    List<BlogVO> getFollowBlogByUserId(Long userId);

    /**
     * 获取用户点赞的博客
     */
    List<BlogVO> getLikeBlogByUserId(Long userId);

    void addMyTeacher(Integer teacherId, Integer studentId);
    //--------------以下是DAO
    List<User> getUserBySchool(String school, RoleEnums roleEnums);

    Map<Long, User> getByUserIdTOMap(List<Long> userIds);

    Long checkLogin();

    User getUserInfo();

    void modifyUserInfo(Long userId, UserInfoDTO userInfoDTO);

    /**
     * 根据学号查找学生
     */
    User getUserByStudentId(String studentNumber);

    UserCount getUserCount(Long userId);

    List<UserDTO> getTeacher(String school);


    String createInvitationCode(EmailLoginDTO emailLoginDTO);


    /**
     * 获取学生信息
     */
    List<User> getStudentInfo();
    Long getStudentCount();


    UserVO getUserVOById(Long userId);

    User updateScoreByUserId(Long userId, Integer score);
}

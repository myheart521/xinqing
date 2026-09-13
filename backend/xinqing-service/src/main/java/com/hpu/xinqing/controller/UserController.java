package com.hpu.xinqing.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hpu.xinqing.mapper.UserMapper;
import com.hpu.xinqing.service.IStudentService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqing.utils.standard.ToStandard;

import com.hpu.xinqingcommon.exception.UserNotLoginException;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingcommon.utils.HuaweiIOTUtil;
import com.hpu.xinqingcommon.utils.StpUtils;
import com.hpu.xinqingpojo.DTO.*;
import com.hpu.xinqingpojo.VO.LoginVO;
import com.hpu.xinqingpojo.VO.TeacherVo;
import com.hpu.xinqingpojo.VO.UserCount;
import com.hpu.xinqingpojo.VO.UserVO;
import com.hpu.xinqingpojo.enmus.RoleEnums;
import com.hpu.xinqingpojo.entity.Province;
import com.hpu.xinqingpojo.entity.Universities;
import com.hpu.xinqingpojo.entity.User;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HuaweiIOTUtil huaweiIOTUtil;
    @Autowired
    ToStandard toStandard;


    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @PostMapping("/student/register")
    public Result studentRegister(@RequestBody RegisterDTO registerDTO) {
        registerDTO.setRoleId(RoleEnums.STUDENT);
        userService.register(registerDTO);
        return Result.success();
    }

    @PostMapping("/teacher/register")
    public Result teacherRegister(@RequestBody RegisterDTO registerDTO) {
        registerDTO.setRoleId(RoleEnums.TEACHER);
        userService.register(registerDTO);
        return Result.success();
    }

    @PostMapping("/modify")
    public Result modify(@RequestBody ModifyUserDTO modifyUserDTO) {
        userService.modifyUser(modifyUserDTO);
        return Result.success();
    }

    @PostMapping("/wxlogin")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    @PostMapping("/accountLogin")
    public Result<LoginVO> login(@RequestBody AccountLoginDTO accountLoginDTO) {
        LoginVO loginVO = userService.accountLogin(accountLoginDTO);
        String iotToken = huaweiIOTUtil.iotLogin();
        SaSession session = StpUtil.getSession();
        session.set("iot-token", iotToken);
        return Result.success(loginVO);
    }

    @PostMapping("/emaillogin")
    public Result<LoginVO> emailLogin(@RequestBody EmailLoginDTO emailLoginDTO) {
        LoginVO loginVO = userService.emailLogin(emailLoginDTO);
        String iotToken = huaweiIOTUtil.iotLogin();
        SaSession session = StpUtil.getSession();
        session.set("iot-token", iotToken);
        return Result.success(loginVO);
    }

    @GetMapping("/province")
    public Result<List<Province>> province() {
        List<Province> provinces = userService.getAllProvince();
        return Result.success(provinces);
    }

    @PostMapping("/province/school")
    public Result<List<Universities>> universities(@RequestBody ShoolByProvinceDTO shoolByProvinceDTO) {
        List<Universities> schools = userService.getSchoolByProvinceId(shoolByProvinceDTO);
        return Result.success(schools);
    }

    @GetMapping("/province/school/teacher")
    public Result<List<UserDTO>> getTeacher(@PathVariable String school) {
        List<UserDTO> teacherList= userService.getTeacher(school);
        return Result.success(teacherList);
    }

    @PostMapping("/checkEmail")
    public Result checkEmail(@RequestBody EmailCheckDTO email) {
        userService.checkEmail(email.getEmail());
        return Result.success();
    }

    @PostMapping("/feedback")
    public Result feedback(@RequestBody FeedbackDTO feedbackDTO) {
        userService.feedback(feedbackDTO);
        return Result.success();
    }


    @GetMapping("/queryUser")
    public Result<UserVO> query() {
        Long l = Long.parseLong(StpUtil.getLoginId().toString());
        User user1 = userMapper.selectById(l);
        UserVO userVO = BeanUtil.copyProperties(user1, UserVO.class);
        return Result.success(userVO);
    }

    @GetMapping("info")
    public Result<Map<String, Object>> getUserInfo() {
        return Result.success(toStandard.userInfo(userService.getUserInfo()));
    }

    @PostMapping("info")
    public Result<?> modifyUserInfo(@RequestBody @Valid UserInfoDTO userInfoDTO) {
        Long userId = StpUtils.userId();
        userService.modifyUserInfo(userId, userInfoDTO);
        return Result.success("修改成功");
    }

    /**
     * 获取用户参与活动、发布动态、参与测评的数量
     */
    @GetMapping("count")
    public Result<UserCount> getUserCount() {
        Long userId = StpUtils.userId();
        if (ObjectUtil.isEmpty(userId)) {
            throw new UserNotLoginException("未登录，请先登录");
        }
        UserCount userCount = userService.getUserCount(userId);
        return Result.success(userCount);
    } @GetMapping("myTeacher")
    public Result<List<TeacherVo>> queryTeacher(@RequestParam String school ){
        List<User> teachers = userService.getUserBySchool(school, RoleEnums.TEACHER);
        ArrayList<TeacherVo> teacherVos = new ArrayList<>();
        for (User teacher:teachers) {
            teacherVos.add(new TeacherVo(teacher.getId(),teacher.getUserName()));
        }
        return Result.success(teacherVos);
    }
    @PostMapping("myTeacher")
    public Result addMyTeacher(@RequestParam Integer teacherId ){
//        Long userId = StpUtils.userId();
        //todo userId先写死
        Integer studentId  = 2;
        userService.addMyTeacher(teacherId,studentId);
        return Result.success();
    }
    @SaCheckRole(value = {"admin"})
    @PostMapping("invite")
    public Result createInvitationCode(@RequestBody EmailLoginDTO emailLoginDTO){
        String invitationCode = userService.createInvitationCode(emailLoginDTO);
        return Result.success(invitationCode);
    }
}

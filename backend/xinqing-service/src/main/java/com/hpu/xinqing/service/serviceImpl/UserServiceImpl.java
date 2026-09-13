package com.hpu.xinqing.service.serviceImpl;

import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.*;
import com.hpu.xinqing.properties.WeChatProperties;
import com.hpu.xinqing.service.*;
import com.hpu.xinqingcommon.constant.RegexConstant;
import com.hpu.xinqingcommon.exception.EmailException;
import com.hpu.xinqingcommon.exception.LoginFailException;
import com.hpu.xinqingcommon.exception.RegisterFailedException;
import com.hpu.xinqingcommon.utils.HttpClientUtil;
import com.hpu.xinqingcommon.utils.MailUtil;
import com.hpu.xinqingcommon.utils.PasswordUtil;
import com.hpu.xinqingcommon.utils.StpUtils;
import com.hpu.xinqingpojo.DTO.*;
import com.hpu.xinqingpojo.VO.*;
import com.hpu.xinqingpojo.enmus.RoleEnums;
import com.hpu.xinqingpojo.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hpu.xinqingcommon.constant.RedisConstant.LOGIN_CODE_KEY;
import static com.hpu.xinqingcommon.constant.RedisConstant.LOGIN_CODE_TTL;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SchoolMapper schoolMapper;
    @Autowired
    private ProvinceMapper provinceMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Resource
    @Lazy
    private IBlogService blogService;
    @Resource
    @Lazy
    private IActivityService activityService;
    @Autowired
    private IStudentService studentService;

    @Resource
    @Lazy
    private ICircleService circleService;
    @Autowired
    private HistoryTestService historyTestService;
    @Autowired
    private StudentMyTeacherMapper studentMyTeacherMapper;

    @Transactional
    public void register(RegisterDTO registerDTO) {


        //用户名6到12位
        if (!ReUtil.isMatch(RegexConstant.USERNAME, registerDTO.getAccount())) {
            throw new RegisterFailedException("学号或工号格式异常");
        }
        //校验确认密码
        if (!registerDTO.getPassword().equals(registerDTO.getCheckPassword())) {
            throw new RegisterFailedException("确认密码应与密码相对应");
        }
        //图片一定要是一个网址
        if (!ReUtil.isMatch(RegexConstant.INTERNET, registerDTO.getUserAvatar())) {
            throw new RegisterFailedException("头像上传异常");
        }
        //校验邮箱
        if (!ReUtil.isMatch(RegexConstant.EMAIL, registerDTO.getEmail())) {

            throw new RegisterFailedException("邮箱输入格式异常");
        }
        User one = lambdaQuery().eq(User::getEmail, registerDTO.getEmail()).one();
        if (one != null) {
            throw new RegisterFailedException("邮箱已经注册");
        }
        //校验密码
        if (!ReUtil.isMatch(RegexConstant.PASSWORD, registerDTO.getPassword())) {
            throw new RegisterFailedException("密码不符合要求");
        }
        //校验验证码
        String code = registerDTO.getCode();
        String s = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + registerDTO.getEmail());
        if (s == null || s.isEmpty()) {
            throw new EmailException("验证码过期或未获取");
        }
        if (!s.equals(code)) {
            throw new EmailException("验证码错误");
        }
        User user = new User();
        BeanUtil.copyProperties(registerDTO, user);
        user.setUserAccount(registerDTO.getAccount());

        if(registerDTO.getRoleId()!=null){
            Integer roleId = registerDTO.getRoleId().getType();
            user.setRoleId(roleId);
        }
        user.setUserPassword(PasswordUtil.encrypt(registerDTO.getPassword()));
        userMapper.insert(user);
        if (registerDTO.getRoleId()==RoleEnums.STUDENT) {
            Student student = new Student();
            student.setUserId(user.getId());
            student.setSchool(user.getSchool());
            studentService.save(student);
        }
    }

    @Override
    public void modifyUser(ModifyUserDTO modifyUserDTO) {
        User user = new User();
        String userRole = modifyUserDTO.getUserRole();
        //校验角色
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("role_name", userRole));
        if (role == null) throw new NotRoleException("角色不存在异常");
        //用户名6到12位
        if (!ReUtil.isMatch(RegexConstant.USERNAME, modifyUserDTO.getUserName())) {
            throw new RegisterFailedException("用户名格式异常");
        }
        //校验确认密码
        if (!modifyUserDTO.getPassword().equals(modifyUserDTO.getCheckPassword())) {
            throw new RegisterFailedException("确认密码应与密码相对应");
        }
        //图片一定要是一个网址
        if (!ReUtil.isMatch(RegexConstant.INTERNET, modifyUserDTO.getUserAvatar())) {
            throw new RegisterFailedException("头像上传异常");
        }
        //简介长度不能大于200字
        if (!ReUtil.isMatch(RegexConstant.PROFILE, modifyUserDTO.getUserProfile())) {
            throw new RegisterFailedException("简介长度异常");
        }
        //校验邮箱
        if (!ReUtil.isMatch(RegexConstant.EMAIL, modifyUserDTO.getEmail())) {
            throw new RegisterFailedException("邮箱输入格式异常");
        }
        //校验省份
        QueryWrapper<Province> provinceQueryWrapper = new QueryWrapper<>();
        Province province = provinceMapper.selectOne(provinceQueryWrapper.eq("name", modifyUserDTO.getProvince()));
        if (province == null) throw new RegisterFailedException("省份名称异常");
        //校验学校
        QueryWrapper<Universities> universitiesQueryWrapper = new QueryWrapper<>();
        universitiesQueryWrapper.eq("name", modifyUserDTO.getSchoolName());
        Universities schoolName = schoolMapper.selectOne(universitiesQueryWrapper);
        if (schoolName == null) throw new RegisterFailedException("学校名称异常");
        //校验性别
        String sex = modifyUserDTO.getSex();
        if (sex == null || sex.length() == 0 || !(sex.equals("男")) && !(sex.equals("女")))
            throw new RegisterFailedException("性别输入异常");
        Long loginId = Long.parseLong(StpUtil.getLoginId().toString());
        user.setId(loginId);
        BeanUtil.copyProperties(modifyUserDTO, user);
        user.setRoleId(role.getId());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    public LoginVO login(LoginDTO loginDTO) {
        String openid = getOpenid(loginDTO);
        //是否曾经注册过
        QueryWrapper queryWrapper = new QueryWrapper<User>();
        queryWrapper.ge("union_id", openid);
        User user = userMapper.selectOne(queryWrapper);
        //未注册过，默认游客
        if (user == null) {
            user = new User();
            user.setUserName("wx_" + UUID.randomUUID());
            user.setUserAvatar("/assets/placeholder.svg");
            user.setUserProfile("不愿意透露姓名的神秘人一枚~");
            user.setUnionId(openid);
            user.setRoleId(4);//设置角色为游客
            userMapper.insert(user);
        }
        StpUtil.login(user.getId());
        String tokenValue = StpUtil.getTokenValue();
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        LoginVO loginVO = new LoginVO(tokenValue, userVO);
        return loginVO;
    }

    @Override
    public LoginVO accountLogin(AccountLoginDTO accountLoginDTO) {
        //是否曾经注册过
        QueryWrapper queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("user_account", accountLoginDTO.getAccount());
        User user = userMapper.selectOne(queryWrapper);
        //未注册过，默认游客
        if (user == null) {
            user = new User();
            BeanUtil.copyProperties(accountLoginDTO, user);
            user.setUserName("wx_" + UUID.randomUUID());
            user.setUserAvatar("/assets/placeholder.svg");
            user.setUserProfile("不愿意透露姓名的神秘人一枚~");
            user.setRoleId(4);
            if (accountLoginDTO.getAccount().equals("admin")) user.setRoleId(1);
            userMapper.insert(user);
        } else {
            if (!PasswordUtil.decrypt(accountLoginDTO.getPassword(), user.getUserPassword()))
                throw new LoginFailException("登陆失败");
        }
        SaLoginModel loginModel = new SaLoginModel();
        loginModel.setExtra("username", user.getUserName());
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        StpUtil.login(user.getId(), loginModel);
        String tokenValue = StpUtil.getTokenValue();
        LoginVO loginVO = new LoginVO(tokenValue, userVO);
        equipmentIdPadding(user);
        return loginVO;
    }

    private void equipmentIdPadding(User user) {
        //      查询设备数据，存储到session中
        List<Equipment> equipmentList = equipmentMapper.selectByMap(Map.of("user_id", user.getId()));
        for (Equipment equipment : equipmentList) {
            String equipId = equipment.getEquipId();
            String[] s = equipId.split("\\$");
            String type = s[0];
            String id = s[1];
            SaSession session = StpUtil.getSession();
            session.set(type, id);
        }
    }

    private String getOpenid(LoginDTO userLoginDTO) {
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type", "authorization_code");
        String s = HttpClientUtil.doGet(WX_LOGIN, map, null);
        JSONObject parse = JSON.parseObject(s);
        String openid = parse.getString("openid");
        return openid;
    }

    @Override
    public List<Province> getAllProvince() {
        QueryWrapper<Province> provinceQueryWrapper = new QueryWrapper<>();
        provinceQueryWrapper.orderBy(true, true, "id");
        return provinceMapper.selectList(provinceQueryWrapper);
    }

    @Override
    public List<Universities> getSchoolByProvinceId(ShoolByProvinceDTO shoolByProvinceDTO) {
        Page<Universities> page = Page.of(shoolByProvinceDTO.getPageNo(), shoolByProvinceDTO.getPageSize());
        QueryWrapper<Universities> universitiesQueryWrapper = new QueryWrapper<>();
        universitiesQueryWrapper.eq("province_id", shoolByProvinceDTO.getProvinceId());
        List<Universities> universities = schoolMapper.selectList(page, universitiesQueryWrapper);
        return universities;
    }

    @Override
    public void checkEmail(String email) {
        //校验邮箱
        if (!ReUtil.isMatch(RegexConstant.EMAIL, email)) {
            throw new EmailException("邮箱输入异常");
        }
        String s = RandomUtil.randomString(6);
        boolean checkCode = MailUtil.sendEmail(email, "心晴APP验证码", "您的本次验证码为：" + s + "，请尽快进行认证，验证码两分钟内有效");
        if (!checkCode) {
            throw new EmailException("邮箱发送异常");
        }
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + email, s, LOGIN_CODE_TTL, TimeUnit.MINUTES);
    }

    @Override
    public void feedback(FeedbackDTO feedbackDTO) {
        Object loginId = StpUtil.getLoginId();
        User user = userMapper.selectById((Long.parseLong(loginId.toString())));
        if (user == null) {
            throw new EmailException("邮件发送用户异常");
        }
        boolean checkCode = MailUtil.sendEmail("maintainer@example.com", user.getUserName() + "的反馈信息", feedbackDTO.getFeedBackMessage());
        if (!checkCode) {
            throw new EmailException("邮箱发送异常");
        }
    }

    @Override
    public LoginVO emailLogin(EmailLoginDTO emailLoginDTO) {
        QueryWrapper queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("email", emailLoginDTO.getEmail());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new LoginFailException("请先注册！");
        }
        if (!PasswordUtil.decrypt(emailLoginDTO.getPassword(), user.getUserPassword()))
            throw new LoginFailException("密码错误，登陆失败");
        SaLoginModel loginModel = new SaLoginModel();
        StpUtil.login(user.getId(), loginModel);
        String tokenValue = StpUtil.getTokenValue();
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        LoginVO loginVO = new LoginVO(tokenValue, userVO);
        equipmentIdPadding(user);
        return loginVO;
    }

//    /**
//     * 获取用户发的博客，三条
//     *
//     * @param userId
//     * @return
//     */
//    @Override
//    public List<BlogAnalyseDTO> getBlogByUserId(Long userId) {
//        List<BlogVO> list = blogService.selectByUserId(userId, 1);
//        if (list == null) {
//            return Collections.emptyList();
//        }
//
//        // 只保留前三条数据
//        list = list.stream().limit(1).toList();
//
//        // 转换为BlogAnalyseDTO
//        List<BlogAnalyseDTO> blogAnalyseList = list.stream()
//                .map(blog -> BeanUtil.copyProperties(blog, BlogAnalyseDTO.class))
//                .collect(Collectors.toList());
//
//        // 根据circleId查询数据库获取circle的名字并设置到BlogAnalyseDTO中
//        for (int i = 0; i < blogAnalyseList.size(); i++) {
//            BlogAnalyseDTO blogAnalyseDTO = blogAnalyseList.get(i);
//            String circleName = circleService.getCircleNameById(Long.valueOf(list.get(i).getCircleId()));
//            blogAnalyseDTO.setCircleName(circleName);
//        }
//        return blogAnalyseList;
//    }


    /**
     * 获取用户发的活动，三条
     *
     * @param userId
     * @return
     */
    @Override
    public List<BlogAnalyseDTO> getBlogByUserId(Long userId) {
        try {
            // Hutool空集合处理（使用CollUtil安全包装）
            List<BlogVO> sourceList = CollUtil.defaultIfEmpty(
                            blogService.selectByUserId(userId, 1),
                            Collections.emptyList()
                    ).stream()
                    .filter(Objects::nonNull)  // Hutool方式：CollUtil.filter()
                    .limit(3)
                    .toList();
            // Hutool BeanUtil安全转换
            List<BlogAnalyseDTO> resultList = new ArrayList<>();
            for (int i = 0; i < sourceList.size(); i++) {
                BlogVO blog = sourceList.get(i);
                // Hutool属性拷贝（带保护机制）
                BlogAnalyseDTO dto = new BlogAnalyseDTO();
                BeanUtil.copyProperties(blog, dto, CopyOptions.create()
                        .ignoreNullValue()  // 忽略源空值
                        .ignoreError()      // 忽略类型转换错误
                        .setOverride(false) // 不覆盖已有值
                );
                // 关键字段设置（使用Hutool工具）
                dto.setTitle(StrUtil.blankToDefault(blog.getTitle(), "未命名博客"));
                dto.setContent(StrUtil.blankToDefault(blog.getContent(), ""));
                // 圈子名称获取（Hutool安全转换）
                Long circleId = Convert.toLong(blog.getCircleId(), 0L);
                String circleName = ObjectUtil.defaultIfNull(
                        circleService.getCircleNameById(circleId),
                        "未知圈子"
                );
                dto.setCircleName(circleName);
                resultList.add(dto);
            }
            return resultList;
        } catch (Exception e) {
            log.error("获取用户博客数据异常 userId={}", userId, e);
            return CollUtil.empty(List.class);  // Hutool空集合
        }
    }

    @Override
    public List<ActiveAnalyseDTO> getActivityByUserId(Long userId) {
        try {
            // Hutool空集合处理（推荐写法）
            List<Activity> sourceList = CollUtil.defaultIfEmpty(
                            activityService.selectActiveListByUserId(userId),
                            Collections.emptyList()
                    ).stream()
                    .filter(Objects::nonNull)
                    .limit(3)
                    .collect(Collectors.toList());

            // Hutool流处理转换
            return CollUtil.map(sourceList, activity -> {
                ActiveAnalyseDTO dto = new ActiveAnalyseDTO();
                // Hutool属性拷贝（强安全模式）
                BeanUtil.copyProperties(activity, dto, CopyOptions.create()
                        .ignoreCase()       // 忽略字段大小写
                        .ignoreNullValue()  // 不拷贝空值
                        .ignoreError()      // 忽略所有错误
                );

                // 关键字段处理
                dto.setTitle(StrUtil.blankToDefault(activity.getTitle(), "未命名活动"));
                dto.setContent(StrUtil.blankToDefault(activity.getContent(), ""));
                dto.setTag(CollUtil.isEmpty(Collections.singleton(activity.getTag())) ?
                        String.valueOf(CollUtil.newArrayList("未知标签")) : activity.getTag());
                dto.setAddress(StrUtil.blankToDefault(activity.getAddress(), "地点未知"));
                return dto;
            }, true);
        } catch (Exception e) {
            log.error("获取用户活动数据异常 userId={}", userId, e);
            return CollUtil.empty(List.class);
        }
    }

    @Override
    public void modifyUserInfo(Long userId, UserInfoDTO userInfoDTO) {
        User user = userMapper.selectById(userId);
        BeanUtil.copyProperties(userInfoDTO, user);
        userMapper.updateById(user);
    }

    @Override
    public User getUserByStudentId(String studentNumber) {
        return userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getStudentNumber, studentNumber));
    }

    @Override
    public UserCount getUserCount(Long userId) {
        //活动数量
        Long activeCount = activityService.selectActiveCountByUserId(userId);
        //动态数量
        Long dynamicCount = blogService.queryBlogCount(userId);
        //完成测评数量
        Long testCount = historyTestService.getHistoryCount(userId);

        return new UserCount(activeCount, dynamicCount, testCount);
    }

    @Override
    public List<User> getStudentInfo() {
        return userMapper.selectList(Wrappers.lambdaQuery(User.class).eq(User::getRoleId, RoleEnums.STUDENT.getType()));
    }

    @Override
    public Long getStudentCount() {
        return userMapper.selectCount(Wrappers.lambdaQuery(User.class)
                .eq(User::getRoleId, RoleEnums.STUDENT.getType()));
    }

    @Override
    public UserVO getUserVOById(Long userId) {

        return BeanUtil.copyProperties(userMapper.selectById(userId), UserVO.class);
    }

    @Override
    public List<UserDTO> getTeacher(String school) {

        List<User> userList = lambdaQuery().eq(User::getSchool, school)
                .eq(User::getRoleId, RoleEnums.TEACHER.getType())
                .list();
        if(userList==null || userList.size()==0){
            throw new RuntimeException("该学校没有教师,没有加入系统");
        }
        return userList.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtil.copyProperties(user, userDTO);
            return userDTO;
        }).collect(Collectors.toList());
    }


    /**
     * 获取用户关注人发的博客，三条
     *
     * @param userId
     * @return
     */
    @Override
    public List<BlogVO> getFollowBlogByUserId(Long userId) {
        return null;
    }

    /**
     * 获取用户点赞的博客，三条
     *
     * @param userId
     * @return
     */
    @Override
    public List<BlogVO> getLikeBlogByUserId(Long userId) {
        return null;
    }

    @Override
    public List<User> getUserBySchool(String school, RoleEnums roleEnums) {
        return lambdaQuery()
                .eq(User::getSchool, school)
                .eq(User::getRoleId, roleEnums.getType())
                .list();


    }

    @Override
    public User getUserInfo() {
        Long userId = StpUtils.userId();
        return userMapper.selectById(userId);

    }

    @Override
    public Map<Long, User> getByUserIdTOMap(List<Long> userIds) {
        List<User> userList = lambdaQuery().in(!userIds.isEmpty(), User::getId, userIds).list();
        Map<Long, User> map = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
        return map;
    }

    @Override
    public Long checkLogin() {
        long userId;
        try {
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        } catch (Exception ex) {
            throw new RuntimeException("请先登录");
        }
        return userId;
    }

    @Override
    public void addMyTeacher(Integer teacherId, Integer studentId) {
        StudentMyTeacher studentMyTeacher = new StudentMyTeacher();
        studentMyTeacher.setTeacherId(teacherId);
        studentMyTeacher.setStudentId(studentId);
        studentMyTeacherMapper.insert(studentMyTeacher);
    }

    @Override
    public String createInvitationCode(EmailLoginDTO emailLoginDTO) {
        Long userId = StpUtils.userId();
        User user = userMapper.selectById(userId);
        if(!user.getEmail().equals(emailLoginDTO.getEmail()))
            throw new LoginFailException("邮箱错误，邀请码生成失败");
        if (!PasswordUtil.decrypt(emailLoginDTO.getPassword(), user.getUserPassword()))
            throw new LoginFailException("密码错误，邀请码生成失败");

        String str = String.valueOf(System.currentTimeMillis());
        //前6位保证随机性，一个用户可邀请最多10^6
        StringBuffer invitationCode = new StringBuffer(str.substring(str.length() - 6));
        //后六位保证唯一性，并且知道邀请码，就可以知道是谁
        String base64code = Base64.getEncoder().encodeToString(userId.toString().getBytes());
        //进一步防止解密，并且使用更加丰富的字符防止暴力破解
        base64code.replace('B','？').replace('C','=').replace('D','&');
        invitationCode.append(base64code);
        return invitationCode.toString();
    }

    @Override
    public User updateScoreByUserId(Long userId, Integer score) {
        boolean update = update().eq("id", userId)
                .setSql("score=score+" + score)
                .update();
        if (update) {
            return getById(userId);
        }
        return null;
    }
}

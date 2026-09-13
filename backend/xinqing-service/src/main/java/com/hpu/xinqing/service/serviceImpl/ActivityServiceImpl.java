package com.hpu.xinqing.service.serviceImpl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.ActivityMapper;
import com.hpu.xinqing.service.IActivityFollowService;
import com.hpu.xinqing.service.IActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingcommon.constant.SystemConstants;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.ActivityDTO;
import com.hpu.xinqingpojo.DTO.UserDTO;
import com.hpu.xinqingpojo.VO.ActivityAllVO;
import com.hpu.xinqingpojo.VO.ActivityVO;
import com.hpu.xinqingpojo.VO.UserVO;
import com.hpu.xinqingpojo.entity.Activity;
import com.hpu.xinqingpojo.entity.ActivityFollow;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动 服务实现类
 * </p>
 *
 * @since 2025-02-18
 */
@Slf4j
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

    @Resource
    private UserService userService;
    @Resource
    @Lazy
    private IActivityFollowService activityFollowService;


    @Override
    public Result add(ActivityDTO activityDTO) {

        long userId;
        try{
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        }catch (Exception ex){
            return Result.error("请先登录");
        }
        Activity activity = BeanUtil.copyProperties(activityDTO, Activity.class);
        String images = String.join(",", activityDTO.getImageList());
        activity.setUserId(userId);
        activity.setImages(images);
        activity.setTag(activityDTO.getLabel());
        boolean save = save(activity);
        if(!save){
            return Result.error("活动发布失败,可能存在错误信息,请重试");
        }
        ActivityFollow activityFollow = new ActivityFollow()
                .setActivityId(activity.getId()).setUserId(userId);
        activityFollowService.save(activityFollow);
        return Result.success("活动发布成功");

    }

    @Override
    @Transactional
    public Result delectById(Long id) {
        long userId;
        try {
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("请重新登录");
        }
        User user = userService.getById(userId);
        if(user==null){
            return Result.error("错误的用户");
        }
        Activity activity = getById(id);
        if(activity==null){
            return Result.error("活动不存在");
        }
        if(activity.getUserId()!=userId && user.getRoleId()!=1){
            return Result.error("您无权删除该评论");
        }
        boolean b =removeById(id);
        if(!b){
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }

    @Override
    @Transactional
    public Result selectById(Long id) {
        Activity activity = getById(id);
        if(activity==null){
            return Result.error("数据错误,不存在的活动");
        }
        ActivityVO activityVO = BeanUtil.copyProperties(activity, ActivityVO.class);
        //对标签,对活动图片
        activityVO.setLabel(activity.getTag());
        activityVO.setMainImage(Arrays.asList(activity.getImages().split(",")));
        //添加举办者用户的信息
        User user = userService.getById(activity.getUserId());
        activityVO.setUserName(user.getUserName());
        activityVO.setUserAvatar(user.getUserAvatar());
        //添加参与者的相关信息
        List<ActivityFollow> list = activityFollowService.lambdaQuery()
                .eq(ActivityFollow::getActivityId, id)
                .orderByAsc(ActivityFollow::getCreateTime)
                .list();
        if(list==null || list.size()==0){
            return null;
        }
        List<Long> userIdList = list.stream().map(activityFollow -> activityFollow.getUserId()).collect(Collectors.toList());
        //根据userIdList查询
        String ids = StrUtil.join(",", userIdList);
        List<User> userList = userService.query().in("id", userIdList).last("order by field(id,"+ids+")").list();
        List<UserDTO> collect = userList.stream().map(user1 -> BeanUtil.copyProperties(user1, UserDTO.class)).collect(Collectors.toList());
        activityVO.setGroupList(collect);
        update().eq("id",id).setSql("view_user_count=view_user_count+1").update();
        return Result.success(activityVO);
    }

    @Override
    public Result selectAll(Integer current) {
        IPage<ActivityAllVO> page = getBaseMapper().selectOrderByHot(new Page<>(current,SystemConstants.DEFAULT_PAGE_SIZE),
                SystemConstants.VIEW,SystemConstants.FOLLOW,SystemConstants.TIME_K
                );

        if(page==null || page.getRecords()==null ||page.getRecords().size()==0) {
          //说明为空
            return Result.success(null);
        }
        List<ActivityAllVO> collect = page.getRecords().stream().map(item -> item.setMainImage(Arrays.asList(item.getImages().split(","))))
                .collect(Collectors.toList());
        return Result.success(collect);
    }

    @Override
    public List<Activity> selectActiveListByUserId(Long id) {
        //根据用户id查询数据库
        return lambdaQuery().eq(Activity::getUserId, id).orderByDesc(Activity::getCreateTime).list();
    }

    @Override
    public List<Activity> selectActiveListByUserId(Long id, Integer limit) {
        return lambdaQuery().eq(Activity::getUserId, id).orderByDesc(Activity::getCreateTime)
                .last("limit "+limit).list();
    }

    @Override
    public Long selectActiveCountByUserId(Long id) {
        return lambdaQuery().eq(Activity::getUserId, id).count();
    }
}

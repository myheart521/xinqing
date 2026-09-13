package com.hpu.xinqing.service.serviceImpl;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.ActivityFollowMapper;
import com.hpu.xinqing.service.IActivityFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.service.IActivityService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingcommon.constant.SystemConstants;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.ActivityVO;
import com.hpu.xinqingpojo.entity.Activity;
import com.hpu.xinqingpojo.entity.ActivityFollow;
import com.hpu.xinqingpojo.entity.CircleFollow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动参与表 服务实现类
 * </p>
 *
 * @since 2025-02-27
 */
@Service
public class ActivityFollowServiceImpl extends ServiceImpl<ActivityFollowMapper, ActivityFollow> implements IActivityFollowService {

    @Resource
    private IActivityService activityService;

    @Resource
    private UserService userService;

    //查询用户是否关注对应的用户,返回true,false
    @Override
    public Boolean queryFollow(Long id) {
        Long userId;
        try{
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        }catch (Exception ex){
            return  Boolean.FALSE;
        }
        ActivityFollow one = query().eq("user_id", userId).eq("activity_id", id).one();
        if(one==null){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 对于未关注的用户直接关注把信息添加到数据库,如果已经关注应该删除数据库中的数据
     * @param id ,follow_user_id
     * @param isFollow
     * @return
     */
    @Override
    @Transactional
    public Result follow(Long id, Boolean isFollow) {

        Long userId;
        try{
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        }catch (Exception ex){
            return Result.error("先登录");
        }
        ActivityFollow one = query().eq("user_id", userId).eq("activity_id", id).one();
        Boolean exist=Boolean.TRUE;
        if (one == null) {
            exist=Boolean.FALSE;
        }
        String result="存在错误";
        if(isFollow){
            //isFollow为true说明需要添加关注
            if(exist){
                return Result.error("不能重复参与");
            }
            //exist为false,说明数据库中不存在,添加数据
            ActivityFollow activityFollow = new ActivityFollow().setActivityId(id)
                    .setUserId(userId);
            boolean save = save(activityFollow);
            if(!save){
                return Result.error("参与失败,请重试");
            }
            boolean update = activityService.update().setSql("follow=follow+1").eq("id", id).update();
            if(!update){
                return Result.error("参与失败,请重试");
            }
            result="参与成功";
        }else {
            //isFollow为false说明需要取消关注,删除对应数据
            if(!exist){
                return Result.error("不能重复取消参与");
            }
//            boolean remove = removeById(f.getId());
            boolean remove = remove(new QueryWrapper<ActivityFollow>().eq("user_id", userId)
                    .eq("activity_id", id));
            if(!remove){
                return Result.error("取消失败,请重试");

            }
            boolean update = activityService.update().setSql("follow=follow+1").eq("id", id).update();
            if(!update){
                return Result.error("取消失败,请重试");
            }
            result="活动取消参与";
        }
        return Result.success(result);

    }

    @Override
    public PageResult queryFollowList(Integer current) {
        Long userId = userService.checkLogin();
        Page<ActivityFollow> page = lambdaQuery().eq(ActivityFollow::getUserId, userId)
                .orderByDesc(ActivityFollow::getCreateTime)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        List<ActivityFollow> records = page.getRecords();
        if(page==null || records ==null || records.size()==0){
            return new PageResult(0L,new ArrayList<>());
        }
        List<Long> activtyIds = records.stream().map(ActivityFollow::getActivityId).collect(Collectors.toList());
        List<Activity> list = activityService.lambdaQuery().in(Activity::getId, activtyIds).list();
        if(list==null || list.size()==0){
            return new PageResult(0L,new ArrayList<>());
        }
        List<ActivityVO> collect = list.stream().map(activity -> {
            ActivityVO activityVO = BeanUtil.copyProperties(activity, ActivityVO.class);
            //对标签,对活动图片
            activityVO.setLabel(activity.getTag());
            activityVO.setMainImage(Arrays.asList(activity.getImages().split(",")));
            return activityVO;
        }).collect(Collectors.toList());
        return new PageResult(page.getTotal(),collect);
    }
}

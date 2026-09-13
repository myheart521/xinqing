package com.hpu.xinqing.service.serviceImpl;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.CircleFollowMapper;
import com.hpu.xinqing.service.ICircleFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.service.ICircleService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingcommon.constant.SystemConstants;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.Circle;
import com.hpu.xinqingpojo.entity.CircleFollow;
import com.hpu.xinqingpojo.entity.Follow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户关注圈子信息表 服务实现类
 * </p>
 *
 * @since 2025-02-22
 */
@Service
public class CircleFollowServiceImpl extends ServiceImpl<CircleFollowMapper, CircleFollow> implements ICircleFollowService {

    @Resource
    private ICircleService circleService;

    @Resource
    private UserService userService;


    //查询用户是否关注对应的用户,返回true,false
    @Override
    public Boolean queryFollow(Long id) {
        Long userId;
        try{
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        }catch (Exception ex){
            return Boolean.FALSE;
        }        CircleFollow f = query().eq("user_id", userId).eq("circle_id", id).one();
        if(f==null){
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
    @Transactional
    @Override
    public Result follow(Long id, Boolean isFollow) {

        Long userId;
        try{
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        }catch (Exception ex){
            return Result.error("先登录");
        }
        String key= RedisConstant.FOLLOW_USER_KEY+userId;
        CircleFollow f = query().eq("user_id", userId).eq("circle_id", id).one();
        Boolean exist=Boolean.TRUE;
        if (f == null) {
            exist=Boolean.FALSE;
        }
        String result="存在错误";
        if(isFollow){
            //isFollow为true说明需要添加关注
            if(exist){
                return Result.error("不能重复关注");
            }
            //exist为false,说明数据库中不存在,添加数据
            CircleFollow circleFollow = new CircleFollow();
            circleFollow.setCircleId(id);
            circleFollow.setUserId(userId);
            boolean save = save(circleFollow);
            if(!save){
                return Result.error("关注失败,请重试");
            }
            boolean update = circleService.update().eq("id", id).setSql("follow=follow+1").update();
            if (!update){
                return Result.error("关注失败,请重试");
            }
            result="关注成功";
        }else {
            //isFollow为false说明需要取消关注,删除对应数据
            if(!exist){
                return Result.error("不能重复取消关注");
            }
//            boolean remove = removeById(f.getId());
            boolean remove = remove(new QueryWrapper<CircleFollow>().eq("user_id", userId)
                    .eq("circle_id", id));
            if(!remove){
                return Result.error("取消失败,请重试");
            }
            boolean update = circleService.update().eq("id", id).setSql("follow=follow-1").update();
            if(!update){
                return Result.error("取消失败,请重试");
            }
            result="关注取消成功";
        }
        return Result.success(result);

    }


    /**
     * 查询关注的圈子列表
     * @param current
     * @return
     */
    @Override
    public PageResult selectList(Integer current) {
         //1.检验是否登录
         Long userId=userService.checkLogin();
         //2.查询用户关注的圈子
        Page<CircleFollow> page = lambdaQuery().eq(CircleFollow::getUserId, userId)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        List<CircleFollow> records = page.getRecords();
        if(page==null || records ==null || records.size()==0){
            return new PageResult(0L,new ArrayList<>());
        }
        //3.信息转换
        List<Long> circleIds = records.stream().map(CircleFollow::getCircleId).collect(Collectors.toList());
        List<Circle> list = circleService.lambdaQuery().in(Circle::getId, circleIds).list();
        return new PageResult(page.getTotal(),list);
    }
}

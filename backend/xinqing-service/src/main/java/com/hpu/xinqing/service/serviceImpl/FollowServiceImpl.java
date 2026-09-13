package com.hpu.xinqing.service.serviceImpl;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.FollowMapper;
import com.hpu.xinqing.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingcommon.constant.SystemConstants;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.UserDTO;
import com.hpu.xinqingpojo.entity.Follow;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户之间关注信息 服务实现类
 * </p>
 *
 * @since 2025-02-22
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    //查询用户是否关注对应的用户,返回true,false
    @Override
    public Boolean queryFollow(Long id) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        Follow f = query().eq("user_id", userId).eq("follow_user_id", id).one();
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
    @Override
    public Result follow(Long id, Boolean isFollow) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        String key= RedisConstant.FOLLOW_USER_KEY+userId;
        Follow f = query().eq("user_id", userId).eq("follow_user_id", id).one();
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
            Follow follow = new Follow();
            follow.setFollowUserId(id);
            follow.setUserId(userId);
            boolean save = save(follow);
            if(save){
                result="关注成功";
                //把关注信息添加到redis中的set集合中用于之后互相关注的查询
                stringRedisTemplate.opsForSet().add(key,id.toString());
            }

        }else {
            //isFollow为false说明需要取消关注,删除对应数据
            if(!exist){
                return Result.error("不能重复取消关注");
            }
//            boolean remove = removeById(f.getId());
            boolean remove = remove(new QueryWrapper<Follow>().eq("user_id", userId)
                    .eq("follow_user_id", id));
            if(remove){
                result="关注取消成功";
                //取消关注,需要把redis中的数据删除
                stringRedisTemplate.opsForSet().remove(key,id.toString());
            }
        }
        return Result.success(result);

    }

    /**
     * 查询我关注的人列表
     * @param count
     * @return
     */
    @Override
    public PageResult followerList(Integer count) {
        //1.获取用户id
        String userId;
        try{
            userId = StpUtil.getLoginId().toString();
        }catch (Exception ex){
            throw new RuntimeException("请先登录");
        }
        long user = Long.parseLong(userId);
        Page<Follow> page = lambdaQuery().eq(Follow::getUserId, user).page(new Page<>(count, SystemConstants.MAX_PAGE_SIZE));
        if (page==null || page.getRecords().isEmpty()) {
            return new PageResult(0L, new ArrayList<>());
        }
        //信息转换
        List<Follow> records = page.getRecords();
        List<Long> userIds = records.stream().map(Follow::getFollowUserId).collect(Collectors.toList());
        List<User> userList = userService.lambdaQuery().in(User::getId, userIds).list();
        List<UserDTO> collect = userList.stream().map(item -> BeanUtil.copyProperties(item, UserDTO.class)).collect(Collectors.toList());
        return new PageResult(page.getTotal(),collect);
    }

}

package com.hpu.xinqing.service.serviceImpl;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.hpu.xinqing.mapper.CommentsMapper;
import com.hpu.xinqing.service.IBlogService;
import com.hpu.xinqing.service.ICommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingcommon.constant.SystemConstants;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.CommentAnalyseDTO;
import com.hpu.xinqingpojo.VO.CommentVO;
import com.hpu.xinqingpojo.entity.Blog;
import com.hpu.xinqingpojo.entity.Comments;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 评论表,包括多级评论 服务实现类
 * </p>
 *
 * @since 2025-02-18
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements ICommentsService {

    @Resource
    private UserService userService;
    @Resource
    private IBlogService blogService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SensitiveWordBs sensitiveWordBs;

    //添加
    @Override
    public Result add(Comments comments) {
        Long userId = userService.checkLogin();
        comments.setUserId(userId);
        //默认的敏感词过滤器

        String replace = sensitiveWordBs.replace(comments.getContent());
        comments.setContent(replace);
        boolean save = save(comments);
        if (!save) {
            return Result.error("系统错误");
        }
        //添加评论成功,需要判断其是否为一级评论
        blogService.update().setSql("comments=comments+1").eq("id", comments.getBlogId()).update();
        Long parentId = comments.getParentId();
        if (parentId == null || parentId == 0) {
            //说明为为一级评论直接返回添加成功即可
            return Result.success(comments);
        }
        //不为空需要更新对应一级评论的数量
        update().setSql("comment_count=comment_count+1")
                .eq("id", parentId).update();

        return Result.success(comments);
    }

    @Override
    public Result delect(Long id) {
        long userId = Long.parseLong(StpUtil.getLoginId().toString());
        try{
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        }catch (Exception ex){
            return Result.error("请先登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Comments comments = getById(id);
        if (comments == null) {
            return Result.error("动态不存在");
        }
        if (comments.getUserId() != userId && user.getRoleId() != 1) {
            return Result.error("您无权删除该评论");
        }
        boolean b = removeById(id);
        if (!b) {
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }

    //为评论点赞
    @Override
    @Transactional
    public Result likeCommentById(Long id) {
        String key = RedisConstant.COMMENT_LIKED_KEY + id;
        //1.获取用户userId
        Long userId;
        try{
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        }catch (Exception ex){
            return Result.error("请先登录");
        }
        //2.判断用户是否已经点赞
        //set集合中可直接利用isMember判断是否存在
        Boolean exist = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
        //Zset中利用ZSCORE,如果不存在返回nil
//        Double score = stringRedisTemplate.opsForSet().
        if (!BooleanUtil.isTrue(exist)) {
            //3.不存在说明用户未点赞
            //3.1 需要把数据库中的liked字段+1
            boolean isSuccess = update().setSql("liked=liked+1").eq("id", id).update();
            if (isSuccess) {
                //3.2把userId添加到redis中的set集合中
                //stringRedisTemplate.opsForSet().add(key, userId.toString());
                //3.2把userId添加到redis中的zset集合中,对应的分数就是时间戳
                stringRedisTemplate.opsForSet().add(key, userId.toString());
                return Result.success("点赞成功");
            }
        } else {
            //4 存在说明用户已经点过赞,需要取消点赞
            //4.1 数据库中liked字段-1
            boolean isSuccess = update().setSql("liked=liked-1").eq("id", id).update();
            if (isSuccess) {
                //4.2 需要把redis中set集合中对应的userId移除
                //stringRedisTemplate.opsForSet().remove(key, userId.toString());
                //需要把redis中Zset集合中对应的userId移除
                stringRedisTemplate.opsForSet().remove(key, userId.toString());
                return Result.success("取消点赞");
            }
        }
        return Result.error("点赞出现错误");
    }

    //一级评论分页查询
    @Override
    public List<CommentVO> queryFirstComment(Long blogId, Integer current) {
        Page<Comments> page = lambdaQuery().eq(Comments::getBlogId, blogId).eq(Comments::getParentId, 0L).orderByDesc(Comments::getCreateTime)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        List<Comments> list = page.getRecords();
        if (page == null || list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        List<CommentVO> commentVOList = list.stream().map(comment -> {
            CommentVO commentVO = BeanUtil.copyProperties(comment, CommentVO.class);
            queryCommentUser(commentVO);
            commentVO.setLikeActive(isCommentLike(comment.getId()));
            return commentVO;
        }).collect(Collectors.toList());
        return commentVOList;
    }

    //子评论分页查询
    @Override
    public List<CommentVO> queryTwoComment(Long parentId, Integer current) {
        Page<Comments> page = lambdaQuery().eq(Comments::getParentId, parentId).orderByDesc(Comments::getCreateTime)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        List<Comments> list = page.getRecords();
        if (page == null || list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        List<CommentVO> commentVOList = list.stream().map(comment -> {
            CommentVO commentVO = BeanUtil.copyProperties(comment, CommentVO.class);
            queryCommentUser(commentVO);
            queryAnswerUserName(commentVO);
            commentVO.setLikeActive(isCommentLike(comment.getId()));
            return commentVO;
        }).sorted(Comparator.comparing(CommentVO::getCreateTime)).collect(Collectors.toList());
        //根据createTime进行排序,日期大的排在后面
        return commentVOList;
    }

    //添加评论的用户信息
    public void queryCommentUser(CommentVO commentVO) {
        Long userId = commentVO.getUserId();
        User user = userService.getById(userId);
        commentVO.setUserName(user.getUserName());
        commentVO.setUserAvatar(user.getUserAvatar());
    }

    //添加回复评论的用户信息
    public void queryAnswerUserName(CommentVO commentVO) {
        Long answerId = commentVO.getAnswerId();
        if(answerId==null || answerId==0){
            return;
        }
        Comments comment = getById(answerId);
        // 回复用户的用户名
        commentVO.setAnswerUserName(userService.getById(comment.getUserId()).getUserName());
        //回复的对应的内容
        commentVO.setAnswerText(comment.getContent());
    }

    //判断用户是否对评论点赞
    public boolean isCommentLike(Long commentId) {
        String userId;
        try{
            userId = StpUtil.getLoginId().toString();
        }catch (Exception ex){
            return Boolean.FALSE;
        }

        String key = RedisConstant.COMMENT_LIKED_KEY + commentId;
        Boolean exit= stringRedisTemplate.opsForSet().isMember(key,userId);
        return BooleanUtil.isTrue(exit);
    }

    @Override
    public PageResult queryBlogByUserId(Integer current) {
        //1.获取用户id
        String userId;
        try{
            userId = StpUtil.getLoginId().toString();
        }catch (Exception ex){
            throw new RuntimeException("请先登录");
        }
        //2.根据用户id查询用户的评论
        //2.1 查询用户的发的贴子
        Long user=Long.parseLong(userId);
        List<Long> blogIds = blogService.selectListByUserId(user);
        //2.2 查询那些是我发,或者我帖子下的评论的评论，
        List<Comments> commentList = lambdaQuery().eq(Comments::getUserId, user)
                .or()
                .in(!blogIds.isEmpty(), Comments::getBlogId, blogIds)
                .list();
        List<Long> ids = commentList.stream().map(Comments::getId).collect(Collectors.toList());
        if(ids==null || ids.size()==0){
            return new PageResult(0L,new ArrayList<>());
        }
        //2.3 查询我发的评论和帖子下面的回复的评论
        Page<Comments> page = lambdaQuery()
                .ne(Comments::getUserId, userId)
                .in(Comments::getParentId,ids)
                .or()
                .in(!blogIds.isEmpty(), Comments::getBlogId, blogIds)
                .orderByDesc(Comments::getCreateTime)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        //3 处理数据返回前端
        // 判断不存在评论
        if(page==null || page.getRecords()==null || page.getRecords().size()==0){
            return new PageResult(0L,new ArrayList<>());
        }
        // 对评论进行处理
        List<Comments> list = page.getRecords();
        // 3.1 准换评论id对应的map集合
        Map<Long, Comments> idMap = list.stream().collect(Collectors.toMap(Comments::getId, comments -> comments));
        //3.2 查询出发送评论的用户信息
//        List<Long> userIds = list.stream().map(Comments::getUserId).collect(Collectors.toList());
//        Map<Long,User> userMap= userService.getByUserIdTOMap(userIds);
        // 3.3 转换信息为CommentVO
        List<CommentVO> commentVOList = list.stream().map(comment -> {
            CommentVO commentVO = BeanUtil.copyProperties(comment, CommentVO.class);
            // 添加用户的信息
            queryCommentUser(commentVO);
            // 添加回复的用户信息
            queryAnswerUserName(commentVO);
            return commentVO;
        }).collect(Collectors.toList());

        return new PageResult(page.getTotal(),list);
    }

    @Override
    public List<Comments> queryCommentByUserId(Long userId, Integer size) {
        return lambdaQuery().eq(Comments::getUserId, userId)
                .orderByDesc(Comments::getCreateTime).last("limit "+size).list();
    }

    @Override
    public List<CommentAnalyseDTO> queryCommentRecentByUserId(Long userId) {
        LocalDateTime endTime = LocalDateTime.now(); // 当前时间
        LocalDateTime startTime = endTime.minusWeeks(1); // 一周前
        List<Comments> list = lambdaQuery().eq(Comments::getUserId, userId)
                .between(Comments::getCreateTime, startTime, endTime)
                .orderByDesc(Comments::getCreateTime).list();

        return BeanUtil.copyToList(list,CommentAnalyseDTO.class);
    }

}

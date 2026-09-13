package com.hpu.xinqing.service.serviceImpl;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.BlogMapper;
import com.hpu.xinqing.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.service.ICommentsService;
import com.hpu.xinqing.service.IFollowService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingcommon.constant.SystemConstants;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.BlogDTO;
import com.hpu.xinqingpojo.VO.BlogMessageVO;
import com.hpu.xinqingpojo.VO.BlogVO;
import com.hpu.xinqingpojo.VO.CommentVO;
import com.hpu.xinqingpojo.VO.UserBlogVO;
import com.hpu.xinqingpojo.entity.Blog;
import com.hpu.xinqingpojo.entity.Comments;
import com.hpu.xinqingpojo.entity.Follow;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 帖子 服务实现类
 * </p>
 *
 * @since 2025-02-18
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserService userService;
    @Resource
    @Lazy
    private ICommentsService commentsService;
    @Resource
    private IFollowService followService;

    //填加动态
    @Override
    public Result add(BlogDTO blogDTO) {
        String userId;
        try {
            userId = StpUtil.getLoginId().toString();
        } catch (Exception ex) {
            return Result.error("请先登录");
        }

        Blog blog = BeanUtil.copyProperties(blogDTO, Blog.class);
        String tag = String.join(",", blogDTO.getTags());
        String images = String.join(",", blogDTO.getImages());
        blog.setUserId(Long.parseLong(userId));
        blog.setImages(images);
        blog.setTag(tag);
        boolean save = save(blog);
        if (!save) {
            return Result.error("发布失败,请重试");
        }
        return Result.success("发布成功");
    }

    @Override
    public Result selectById(Long id) {
        Blog blog = getById(id);
        if (blog == null) {
            return Result.error("帖子不存在");
        }
        BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
        queryBolgUser(blogVO);
        blogVO.setIsLike(isBolgLike(id));
       /* List<Comments> list = commentsService.lambdaQuery().eq(Comments::getBlogId, id).eq(Comments::getParentId, 0L).list();
        List<CommentVO> commentVOList = list.stream().map(comment -> {
            CommentVO commentVO = BeanUtil.copyProperties(comment, CommentVO.class);
            queryCommentUser(commentVO);
            commentVO.setLikeActive(isCommentLike(comment.getId()));
            return commentVO;
        }).collect(Collectors.toList());*/
        List<CommentVO> commentVOList = commentsService.queryFirstComment(id, 1);
        List<String> tags = Arrays.asList(blog.getTag().split(","));
        blogVO.setLabel(tags);
        blogVO.setMainImage(Arrays.asList(blog.getImages().split(",")));
        blogVO.setCommentData(commentVOList);
        update().setSql("view_user_count=view_user_count+1").eq("id", id).update();
        return Result.success(blogVO);
    }

    @Override
    public List<BlogVO> selectByUserId(Long userId, Integer current) {
        Page<Blog> page = lambdaQuery().eq(Blog::getUserId, userId).orderByDesc(Blog::getCreateTime)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        List<Blog> blogList = page.getRecords();
        if (blogList == null || blogList.size() == 0) {
            return new ArrayList<>();
        }
        //存在数据
        List<BlogVO> blogVOList = blogList.stream().map(blog -> {
            BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
            queryBolgUser(blogVO);
            blogVO.setIsLike(isBolgLike(blog.getId()));
            List<String> tags = Arrays.asList(blog.getTag().split(","));
            blogVO.setLabel(tags);
            blogVO.setMainImage(Arrays.asList(blog.getImages().split(",")));
            List<String> images = queryLikedUser(blog.getId());
            blogVO.setLikedUser(images);
            return blogVO;
        }).collect(Collectors.toList());

        return blogVOList;
    }

    /**
     * 获取最新的count数量的博客
     * @param userId 用户id
     * @param count 数目
     * @return
     */
    @Override
    public List<BlogVO> selectBlogByUserId(Long userId, Integer count) {
        List<Blog> blogList = lambdaQuery().eq(Blog::getUserId, userId).orderByDesc(Blog::getCreateTime)
                .last("limit " + count).list();
        if (blogList == null || blogList.size() == 0) {
            return new ArrayList<>();
        }
        //存在数据
        return blogList.stream().map(blog -> {
            BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
            queryBolgUser(blogVO);
            blogVO.setIsLike(isBolgLike(blog.getId()));
            List<String> tags = Arrays.asList(blog.getTag().split(","));
            blogVO.setLabel(tags);
            return blogVO;
        }).toList();
    }

    @Override
    public Result queryUserDate(Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("不存在的用户");
        }
        UserBlogVO userBlogVO = new UserBlogVO();
        List<String> avatar = new ArrayList<>();
        if (user.getUserAvatar() != null) {
            avatar = Arrays.asList((user.getAvatars() + user.getUserAvatar()).split(","));
        }
        userBlogVO.setAvatar(avatar);
        userBlogVO.setUsername(user.getUserName());
        userBlogVO.setDesc(user.getUserProfile());
        return Result.success(userBlogVO);

    }

    @Override
    public Result queryUserDateMessage(Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("不存在的用户");
        }
//        BlogMessageVO blogMessageVO = new BlogMessageVO();
        BlogMessageVO blogMessageVO = getBaseMapper().selectMessage(userId);
        //查询粉丝数
        blogMessageVO.setFansCount(followService.lambdaQuery()
                .eq(Follow::getFollowUserId, userId).count());
        //查询关注数
        blogMessageVO.setFocusCount(followService.lambdaQuery()
                .eq(Follow::getUserId, userId).count());
        return Result.success(blogMessageVO);
    }

    /**
     * 根据标签查询blog数据
     *
     * @param tag
     * @return
     */
    @Override
    public Result searchByTag(String tag, Long circleId, Integer current) {
        Page<Blog> page = lambdaQuery()
                .like(!StrUtil.isEmpty(tag), Blog::getTag, tag)
                .eq(circleId != null, Blog::getCircleId, circleId)
                .orderByDesc(Blog::getCreateTime)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        List<Blog> blogList = page.getRecords();
        if (blogList == null || blogList.size() == 0) {
            return Result.success();
        }
        //存在数据
        List<BlogVO> blogVOList = blogList.stream().map(blog -> {
            BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
            queryBolgUser(blogVO);
            blogVO.setIsLike(isBolgLike(blog.getId()));
            List<String> tags = Arrays.asList(blog.getTag().split(","));
            blogVO.setLabel(tags);
            blogVO.setMainImage(Arrays.asList(blog.getImages().split(",")));
            List<String> images = queryLikedUser(blog.getId());
            blogVO.setLikedUser(images);
            return blogVO;
        }).collect(Collectors.toList());
        return Result.success(blogVOList);
    }

    @Override
    public Result searchByContent(String text, Long circleId, Integer current) {
        Page<Blog> page = lambdaQuery()
                .like(!StrUtil.isEmpty(text), Blog::getContent, text)
                .eq(circleId != null, Blog::getCircleId, circleId)
                .orderByDesc(Blog::getCreateTime)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        List<Blog> blogList = page.getRecords();
        if (blogList == null || blogList.size() == 0) {
            return Result.success();
        }
        //存在数据
        List<BlogVO> blogVOList = blogList.stream().map(blog -> {
            BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
            queryBolgUser(blogVO);
            blogVO.setIsLike(isBolgLike(blog.getId()));
            List<String> tags = Arrays.asList(blog.getTag().split(","));
            blogVO.setLabel(tags);
            blogVO.setMainImage(Arrays.asList(blog.getImages().split(",")));
            List<String> images = queryLikedUser(blog.getId());
            blogVO.setLikedUser(images);
            return blogVO;
        }).collect(Collectors.toList());
        return Result.success(blogVOList);
    }

    //对笔记点赞
    //把对笔记点赞的用户id存入redis中的set集合中,保证不重复,同时mysql数据库中blog表中likeed字段需要+1
    @Override
    @Transactional
    public Result likeBlogById(Long id) {
        String key = RedisConstant.BLOG_LIKED_KEY + id;
        //1.获取用户userId
        Long userId;
        try {
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        } catch (Exception ex) {
            return Result.error("先登录");
        }
        //2.判断用户是否已经点赞
        //set集合中可直接利用isMember判断是否存在
        //Boolean exist = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
        //Zset中利用ZSCORE,如果不存在返回nil
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if (score == null || score.isNaN()) {
            //3.不存在说明用户未点赞
            //3.1 需要把数据库中的liked字段+1
            boolean isSuccess = update().setSql("liked=liked+1").eq("id", id).update();
            if (isSuccess) {
                //3.2把userId添加到redis中的set集合中
                //stringRedisTemplate.opsForSet().add(key, userId.toString());
                //3.2把userId添加到redis中的zset集合中,对应的分数就是时间戳
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
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
                stringRedisTemplate.opsForZSet().remove(key, userId.toString());
                return Result.success("取消点赞");
            }
        }
        return Result.error("点赞出现错误");
    }

    @Override
    @Transactional
    public Result delectById(Long id) {
        long userId;
        try {
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        } catch (Exception ex) {
            return Result.error("请先登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Blog blog = getById(id);
        if (blog == null) {
            return Result.error("动态不存在");
        }
        if (blog.getUserId() != userId && user.getRoleId() != 1) {
            return Result.error("您无权删除该评论");
        }
        boolean b = removeById(id);
        if (!b) {
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }


    public List<String> queryLikedUser(Long id) {
        //构造redis中Zset集合的key
        String key = RedisConstant.BLOG_LIKED_KEY + id;
        //去redis中查询,range默认是按分数从小到大排序,直接取前五个即可
        Set<String> set = stringRedisTemplate.opsForZSet().range(key, 0, 5);
        if (set.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> userIds = set.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        String ids = StrUtil.join(",", userIds);
        List<User> userList = userService.query().in("id", userIds).last("order by field(id," + ids + ")").list();
        List<String> images = userList.stream().map(user -> user.getUserAvatar()).collect(Collectors.toList());
        return images;
        //order by field(id,1,2,3,4)
    }

    //添加动态的用户信息
    public void queryBolgUser(BlogVO blogVO) {
        Long userId = blogVO.getUserId();
        User user = userService.getById(userId);
        blogVO.setUserName(user.getUserName());
        blogVO.setUserAvatar(user.getUserAvatar());
    }

    //判断读者是否已经对笔记点赞
    public boolean isBolgLike(Long blogId) {
        String userId;
        try {
            userId = StpUtil.getLoginId().toString();
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
        String key = RedisConstant.BLOG_LIKED_KEY + blogId;
        Double score = stringRedisTemplate.opsForZSet().score(key, userId);
        return score != null && !score.isNaN();
    }

    @Override
    public List<Long> selectListByUserId(Long userId) {
        List<Blog> list = lambdaQuery().eq(Blog::getUserId, userId).select(Blog::getId).list();
        if (list == null || list.size() == 0) {
            return null;
        }
        List<Long> collect = list.stream().map(Blog::getId).collect(Collectors.toList());
        return collect;
    }

    @Override
    public PageResult queryMyLike(Integer current) {
  /*      //检验用户登录
        Long userId = userService.checkLogin();
        //查询用户点赞的动态id
        String key = RedisConstant.BLOG_LIKED_KEY + userId;
        //去redis中查询,range默认是按分数从小到大排序,直接取前五个即可
        Set<String> set = stringRedisTemplate.opsForZSet().range(key, 0, 5);
*/
        return new PageResult(0L, new ArrayList<>());
    }

    @Override
    public Long queryBlogCount(Long userId) {
        return lambdaQuery().eq(Blog::getUserId, userId).count();
    }

    @Override
    public BlogVO getBlogVOById(Long id) {

        Blog blog = getById(id);
        BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
        return blogVO;
    }
}

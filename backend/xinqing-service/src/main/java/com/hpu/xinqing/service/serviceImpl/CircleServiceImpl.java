package com.hpu.xinqing.service.serviceImpl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.CircleMapper;
import com.hpu.xinqing.service.IBlogService;
import com.hpu.xinqing.service.ICircleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.service.ICommentsService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.BlogVO;
import com.hpu.xinqingpojo.VO.CircleVO;
import com.hpu.xinqingpojo.entity.Blog;
import com.hpu.xinqingpojo.entity.Circle;
import com.hpu.xinqingpojo.entity.Comments;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 圈子 服务实现类
 * </p>
 *
 * @since 2025-02-18
 */
@Service
public class CircleServiceImpl extends ServiceImpl<CircleMapper, Circle> implements ICircleService {

    @Resource
    private UserService userService;
    @Resource
    private IBlogService blogService;


    //根据关注的人数查询
    @Override
    public Result getTop() {
        //查数据库
        Page<Circle> page = lambdaQuery().orderByDesc(Circle::getFollow).page(new Page<>(0, 6));
        if(page==null){
            return Result.error("圈子无数据");
        }
        List<Circle> records = page.getRecords();
        List<Circle> list = records.stream().map(circle ->
                circle.setText(circle.getFollow() + "人关注")
        ).collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result getAll() {
        //查数据库
        List<Circle> list = lambdaQuery().orderByDesc(Circle::getFollow).list();
        if(list==null || list.size()==0){
            return Result.error("圈子无数据");
        }
        List<Circle> circles = list.stream().map(circle ->
                circle.setText(circle.getFollow() + "人关注")
        ).collect(Collectors.toList());
        return Result.success(circles);
    }

    /**
     * 查询圈子根据id
     * @param id
     * @return
     */
    @Override
    public Result queryById(Long id) {
        Circle circle = getById(id);
        if(circle==null){
            return Result.error("对应圈子已删除");
        }
        //查询圈子的具体信息
        CircleVO circleVO = BeanUtil.copyProperties(circle, CircleVO.class);
        Page<Blog> page = blogService.lambdaQuery().eq(Blog::getCircleId, id)
                .orderByDesc(Blog::getCreateTime)
                .page(new Page<>(1, 5));
        List<Blog> blogList = page.getRecords();
        List<BlogVO> list = blogList.stream().map(blog -> {
            BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
            blogService.queryBolgUser(blogVO);
            blogVO.setIsLike(blogService.isBolgLike(blog.getId()));
            blogVO.setLabel(Arrays.asList(blog.getTag().split(",")));
            blogVO.setMainImage(Arrays.asList(blog.getImages().split(",")));
            blogVO.setLikedUser(blogService.queryLikedUser(blog.getId()));
            return blogVO;
        }).collect(Collectors.toList());
        circleVO.setBlogList(list);

        return Result.success(circleVO);
    }

    @Override
    public String getCircleNameById(Long id) {
        Circle circle = getById(id);
        return circle.getName();
    }
}

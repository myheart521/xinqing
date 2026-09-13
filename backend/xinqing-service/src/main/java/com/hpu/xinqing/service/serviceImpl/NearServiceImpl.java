package com.hpu.xinqing.service.serviceImpl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.service.IBlogService;
import com.hpu.xinqing.service.NearService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingcommon.constant.SystemConstants;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.BlogVO;
import com.hpu.xinqingpojo.VO.UserNearVO;
import com.hpu.xinqingpojo.entity.Blog;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.Resource;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hpu.xinqingcommon.constant.RedisConstant.GEO_RADIUS;

/**
 * <p>
 * 附近 服务实现类
 * </p>
 *
 * @since 2025-02-18
 */
@Service
public class NearServiceImpl implements NearService {
    @Resource
    private UserService userService;

    @Resource
    private IBlogService blogService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取附近用户
     *
     * @param y 对应 维度 latitude
     * @param x longitude对应 经度
     * @return
     */
    @Override
    public Result queryUserByGeo(Double x, Double y) {
        //1.判断坐标是否为空
        if (x == null || y == null) {
            //说明不用根据距离查
            // 根据类型分页查询
            // TODO:无坐标直接返回null,后期更改
            return Result.success();
        }
        //关于用户的位置信息只能存储一天,其他人只能查看这一天进入附近功能的用户
        String key = RedisConstant.NEAR_KEY + LocalDateTime.now().getDayOfMonth();
        //GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
        //查询5000m之内的用户信息

        GeoResults<RedisGeoCommands.GeoLocation<String>> search = stringRedisTemplate.opsForGeo()
                .search(key, GeoReference.fromCoordinate(x, y), new Distance(GEO_RADIUS)
                        , RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().includeCoordinates());//geosearch的没有分页只能截取到end

        if (search == null) {
            //如果search为空,说明没有符合的数据,直接返回
            return Result.success();
        }
        //4.手动分页,对Search结果进行截取,同时解析出userId和distance和x,y
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = search.getContent();

        List<UserNearVO> collect = new ArrayList<>();
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());

        List<Long> ids = new ArrayList<>();
        Map<Long, Point> map = new HashMap<>();
        Map<Long, Double> doubleMap = new HashMap<>();
        list.stream().forEach(geo -> {
            //获取对应的userId
            String idStr = geo.getContent().getName();
            Long id = Long.valueOf(idStr);
            if (id != userId) {
                ids.add(id);
                //获取对应的经纬度
                map.put(id, geo.getContent().getPoint());
                //获取距离
                doubleMap.put(id, geo.getDistance().getValue());
            }

        });
        if (ids.size() > 0) {
            //5.根据Id查询shop
            String idStr = StrUtil.join(",", ids);
            List<User> userList = userService.query().in("id", ids).last("order by field(id," + idStr + ")").list();
            //6.把对应的距离添加到对应的用户信息
            collect = userList.stream().map(user ->
                    new UserNearVO().setId(user.getId())
                            .setUrl(user.getUserAvatar())
                            .setName(user.getUserName())
                            .setSex(StrUtil.equals(user.getSex(), "男") ? "icon-sex-male" : "icon-sex-female")
                            .setDesc(user.getUserProfile())
                            .setLongitude(map.get(user.getId()).getX())
                            .setLatitude(map.get(user.getId()).getY())
                            .setDistance(doubleMap.get(user.getId()))
            ).collect(Collectors.toList());
        }
        //将用户的坐标信息存入redis
        stringRedisTemplate.opsForGeo().add(key, new Point(x, y), userId.toString());
        if (!stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.expire(key, 1L, TimeUnit.DAYS);
        }

        //6.返回
        return Result.success(collect);
    }

    @Override
    public Result queryBlogByGeo(Integer current, Double longitude, Double latitude) {
        //1.判断坐标是否为空
        if (longitude == null || latitude == null) {
            //说明不用根据距离查
            // TODO:无坐标直接返回null,后期可能更改
            return Result.success();
        }

        //关于用户的位置信息只能存储一天,其他人只能查看这一天进入附近功能的用户
        String key = RedisConstant.NEAR_KEY + LocalDateTime.now().getDayOfMonth();
        //GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
        //查询5000m之内的用户信息
        GeoResults<RedisGeoCommands.GeoLocation<String>> search = stringRedisTemplate.opsForGeo()
                .search(key, GeoReference.fromCoordinate(longitude, latitude), new Distance(GEO_RADIUS)
                        , RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance());//geosearch的没有分页只能截取到end

        if (search == null) {
            //如果search为空,说明没有符合的数据,直接返回
            return Result.success();
        }
        //4.手动分页,对Search结果进行截取,同时解析出userId和distance和x,y
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = search.getContent();

        List<BlogVO> blogVOList = new ArrayList<>();

        List<Long> ids = new ArrayList<>();
        Map<Long, Double> doubleMap = new HashMap<>();//存储距离
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        list.stream().forEach(geo -> {
            //获取对应的userId
            String idStr = geo.getContent().getName();
            Long id = Long.valueOf(idStr);
            if (id != userId) {
                ids.add(id);
                //获取距离
                doubleMap.put(id, geo.getDistance().getValue());
            }
        });
        if (ids.size() > 0) {
            blogVOList = selectByUserIds(ids, current);
            for (BlogVO blogVO : blogVOList) {
                blogVO.setDistance(doubleMap.get(blogVO.getUserId()));
            }
        }
        //6.返回
        return Result.success(blogVOList);
    }

    public List<BlogVO> selectByUserIds(List<Long> ids, Integer current) {
        Page<Blog> page = blogService.lambdaQuery().in(Blog::getUserId, ids).orderByDesc(Blog::getCreateTime)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        List<Blog> blogList = page.getRecords();
        if (blogList == null || blogList.size() == 0) {
            return null;
        }
        //存在数据
        List<BlogVO> blogVOList = blogList.stream().map(blog -> {
            BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
            blogService.queryBolgUser(blogVO);
            blogVO.setIsLike(blogService.isBolgLike(blog.getId()));
            List<String> tags = Arrays.asList(blog.getTag().split(","));
            blogVO.setLabel(tags);
            blogVO.setMainImage(Arrays.asList(blog.getImages().split(",")));
            List<String> images = blogService.queryLikedUser(blog.getId());
            blogVO.setLikedUser(images);
            return blogVO;
        }).collect(Collectors.toList());

        return blogVOList;
    }
}

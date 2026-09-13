package com.hpu.xinqing.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.mapper.BannerMapper;
import com.hpu.xinqing.mapper.DietMapper;
import com.hpu.xinqing.mapper.NewKnowledgeMapper;
import com.hpu.xinqing.mapper.SwiperMapper;
import com.hpu.xinqing.service.SwiperService;
import com.hpu.xinqing.service.TeacherService;
import com.hpu.xinqing.utils.standard.ToStandard;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingcommon.utils.StpUtils;
import com.hpu.xinqingpojo.DTO.*;
import com.hpu.xinqingpojo.VO.LoginVO;
import com.hpu.xinqingpojo.entity.*;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teacher")
@Slf4j
@Validated
public class TeacherController {

    @Autowired
    private TeacherService teacherService;
    @Resource
    NewKnowledgeMapper newKnowledgeMapper;
    @Resource
    DietMapper dietMapper;
    @Resource
    BannerMapper bannerMapper;

    @Resource
    SwiperMapper swiperMapper;
    @Autowired
    SwiperService swiperService;
    @Autowired
    ToStandard toStandard;




    /**
     * 老师获取用户列表
     */
    @GetMapping("stu/list")
    public Result<?> getStuList(){
        Long userId = StpUtils.userId();
        Assert.notNull(userId,"用户未登录, 或登录过期");
        List<User> users = teacherService.getStuList(userId);
        return Result.success(toStandard.users(users));
    }

    /**
     * account, tag
     * @param stuId
     * @param params
     * @return
     */
    private final List<String> userInfoParams=List.of("main");
    @GetMapping("/info/main/{stuId}")
    public Result<?> getStuInfo(@PathVariable Long stuId,
                                @Nullable @RequestParam List<String> params){
        List<String> paramsFinal = Optional.ofNullable(params).orElse(userInfoParams);
        return Result.success(teacherService.getStuInfo(stuId,paramsFinal));
    }



    /**
     * 发布优秀文章
     * params: article或者diet
     */
    @GetMapping("article")
    public Result<?> getArticle(@RequestBody BasePageDTO pageDTO,@RequestParam String type){
        BasePageDTO.legal(pageDTO);//合理化一下BasePageDTO
        Long userId = StpUtils.userId();
        Assert.notNull(userId,"用户未登录, 或登录过期");
        return Result.success(teacherService.getArticle(userId,pageDTO,type));
    }

    //发布优秀文章
    @PostMapping("/publish/article")
    public Result<?> publishArticle(@RequestBody NewKnowledgeDTO article){
        if (!article.isMainNotNull()){
            return Result.error("有必要参数为空");
        }
        long userId = Long.parseLong(StpUtil.getLoginId().toString());
        article.setCreateId(userId);
        teacherService.publish(article);
        return Result.success();
    }
    //删除优秀文章
    @DeleteMapping("/article/{articleId}")
    public Result<?> deleteArticle(@PathVariable Long articleId){
        NewKnowledge article = newKnowledgeMapper.selectById(articleId);
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        if (userId.equals(article.getCreateId())){
            newKnowledgeMapper.deleteById(articleId);
            return Result.success();
        }
        return Result.error("你没权限删除哥们");

    }

    /**
     * 发布食谱
     */
    @PostMapping("/publish/diet")
    public Result<?> publishDiet(@RequestBody DietDTO dietDTO){
        if (!dietDTO.isMainNotNull()){
            return Result.error("有必要参数为空");
        }
        dietDTO.setUserId(Long.parseLong(StpUtil.getLoginId().toString()));
        teacherService.publishDiet(dietDTO);
        return Result.success();
    }
    @DeleteMapping("/diet/{dietId}")
    public Result<?> deleteDiet(@PathVariable Long dietId){
        Diet diet = dietMapper.selectById(dietId);
        Long userId= Long.parseLong(StpUtil.getLoginId().toString());
        if (userId.equals(diet.getUserId())){
            dietMapper.deleteById(dietId);
            return Result.success();
        }
        return Result.error("你没权限删除哥们");
    }


    /**
     * 大轮播图
     */
    @GetMapping("/banner")
    public Result<?> getBanner(){
        List<Banner> banners = bannerMapper.selectList(new QueryWrapper<>());
        return Result.success(toStandard.banners(banners));
    }
    @PostMapping("/publish/banner")
    public Result<?> publishBanner(@RequestBody BannerDTO bannerDTO) {
        if (!bannerDTO.isMainNotNull()){
            return Result.error("有必要参数为空");
        }
        bannerDTO.setCreateId(Long.parseLong(StpUtil.getLoginId().toString()));
        Banner banner = new Banner();
        BeanUtils.copyProperties(bannerDTO,banner);
        teacherService.publishBanner(banner);
        return Result.success();
    }

    @DeleteMapping("/banner/{bannerId}")
    public Result<?> deleteBanner(@PathVariable Long bannerId){
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        Banner banner = bannerMapper.selectById(bannerId);
        if (!banner.getCreateId().equals(userId)) return Result.error("你没权限删除哥们");

        bannerMapper.deleteById(bannerId);
        return Result.success();
    }


    /**
     * card轮播图
     */
    @GetMapping("/card")
    public Result<?> getCard(){
        List<Swiper> swipers = swiperMapper.selectList(new QueryWrapper<>());
        return Result.success(toStandard.swipers(swipers));
    }
    @PostMapping("/publish/card")
    public Result<?> publishCard(SwiperDTO swiperDTO){
        if (!swiperDTO.isMainNotNull()) return Result.error("有必要参数为空");
        if (!swiperDTO.isLinkOK()) return Result.error("link参数错误, 只能是sport或diet");
        swiperDTO.setCreateId(Long.parseLong(StpUtil.getLoginId().toString()));
        swiperService.publishCard(swiperDTO);
        return Result.success();
    }

    @DeleteMapping("/card/{cardId}")
    public Result<?> deleteCard(@PathVariable Long cardId){
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        Swiper swiper = swiperMapper.selectById(cardId);
        if (!swiper.getCreateId().equals(userId)) return Result.error("你没权限删除哥们");
        swiperMapper.deleteById(cardId);
        return Result.success();
    }




}

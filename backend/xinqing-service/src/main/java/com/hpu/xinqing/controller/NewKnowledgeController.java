package com.hpu.xinqing.controller;

import com.hpu.xinqing.service.NewKnowledgeService;
import com.hpu.xinqing.utils.standard.ToStandard;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.HotArticleVO;
import com.hpu.xinqingpojo.VO.NewKnowledgeInfoVo;
import com.hpu.xinqingpojo.VO.NewKnowledgeListVO;
import com.hpu.xinqingpojo.entity.NewKnowledge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/knowledge/new")
public class NewKnowledgeController {
    @Autowired
    private NewKnowledgeService newKnowledgeService;
    @Autowired
    ToStandard toStandard;
    @Autowired
    RedisTemplate redisTemplate;
    @GetMapping("/pages")
    public Result getKnowledgePages(PageDTO pageDTO){
        List<NewKnowledgeListVO> newKnowledgeVOS= newKnowledgeService.pageQuery(pageDTO);
        return Result.success(newKnowledgeVOS);
    }
    @GetMapping("/{id}")
    public Result getDietPages(@PathVariable Integer id){
        NewKnowledgeInfoVo newKnowledgeInfoVo  = newKnowledgeService.view(id);
        return Result.success(newKnowledgeInfoVo);
    }

    //点赞
    @PutMapping("/like/{id}")
    public Result like(@PathVariable Integer id){
        newKnowledgeService.like(id);
        return Result.success();
    }
    //收藏
    @PutMapping("/collection/{id}")
    public Result collection(@PathVariable Integer id){
        newKnowledgeService.collection(id);
        return Result.success();
    }

    //从redis中取出热门id，然后根据id去数据库中查询
    @GetMapping("/hot")
    public Result<List<HotArticleVO>> getHotKnowledge(){
        List<Long> hotArticleIds = redisTemplate.opsForList().range(RedisConstant.HOT_ARTICLE_NEW_KNOWLEDGE, 0, -1);
        List<NewKnowledge> articles = newKnowledgeService.getListByIds(hotArticleIds);
        return Result.success(toStandard.hotArticles(articles));
    }
    @GetMapping("/pages/collections")
    public Result getKnowledgeCollectionsPages(PageDTO pageDTO){
        List<NewKnowledgeListVO> newKnowledgeVOS= newKnowledgeService.pageCollectionQuery(pageDTO);
        return Result.success(newKnowledgeVOS);
    }



}

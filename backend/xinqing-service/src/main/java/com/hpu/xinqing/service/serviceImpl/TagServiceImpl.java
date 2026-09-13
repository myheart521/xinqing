package com.hpu.xinqing.service.serviceImpl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hpu.xinqing.mapper.TagMapper;
import com.hpu.xinqing.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.service.ITagTypeService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.TagListVO;
import com.hpu.xinqingpojo.VO.TagVO;
import com.hpu.xinqingpojo.entity.Tag;
import com.hpu.xinqingpojo.entity.TagType;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 标签 服务实现类
 * </p>
 *
 * @since 2025-02-18
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ITagTypeService tagTypeService;
    @Override
    public Result getTop() {
        String key="cache:tag";
        List<TagVO> tagList=new ArrayList<>(10);
        //redis中查询tag
        Set<String> set = stringRedisTemplate.opsForZSet().rangeByScore(key, 0, Double.MAX_VALUE, 0, 10);
        if(set!=null && set.size()!=0){
            //不为空
            List<TagVO> list = set.stream()
                    .map(jsonStr -> JSONUtil.toBean(jsonStr, TagVO.class))
                    .collect(Collectors.toList());
            return Result.success(list);
        }
        //为空,需要查mysql添加到redis中
        List<Tag> list = query().orderByAsc("sort").list();
        if(list==null||list.size()==0){
            return Result.error("标签无数据");
        }
        int i=0;
        for (Tag tag : list) {
            stringRedisTemplate.opsForZSet().add(key,JSONUtil.toJsonStr(tag),tag.getSort());
            if(i<10){
                TagVO tagVO = BeanUtil.copyProperties(tag, TagVO.class);
                tagList.add(tagVO);
                i++;
            }
        }

        return Result.success(tagList);
    }

    @Override
    public Result getAll() {
       //查询全部的直接去mysql中查
        List<Tag> list = query().list();
        if(list==null|| list.size()==0){
            return Result.error("标签无数据");
        }
        List<TagListVO> tagList=new ArrayList<>();
        Map<Integer, List<Tag>> map = list.stream().collect(Collectors.groupingBy(Tag::getTypeId));
        List<TagType> tagTypes = tagTypeService.list();
        if(tagTypes==null|| tagTypes.size()==0){
            return Result.error("标签类型无数据");
        }
        for (TagType tagType : tagTypes) {
            TagListVO tagListVO = new TagListVO();
            tagListVO.setTitle(tagType.getName());
            List<Tag> tags = map.get(tagType.getId());
            List<TagVO> tagVOList = BeanUtil.copyToList(tags, TagVO.class);
            tagListVO.setList(tagVOList);
            tagList.add(tagListVO);
        }
        return Result.success(tagList);
    }
}

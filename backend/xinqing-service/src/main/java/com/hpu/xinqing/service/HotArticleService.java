package com.hpu.xinqing.service;

import com.hpu.xinqingpojo.entity.NewKnowledge;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

public interface HotArticleService {

    //返回值是n个热点文章的id(没有入参, 因为从数据库中查表)
    public List<Long> getHotArticleId(List<NewKnowledge> articles,Integer wants);

    public boolean support(String type);
}

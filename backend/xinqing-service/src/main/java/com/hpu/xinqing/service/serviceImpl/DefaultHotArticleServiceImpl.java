package com.hpu.xinqing.service.serviceImpl;

import com.hpu.xinqing.service.HotArticleService;
import com.hpu.xinqing.service.NewKnowledgeService;
import com.hpu.xinqingpojo.entity.NewKnowledge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
@Component
@Slf4j
public class DefaultHotArticleServiceImpl implements HotArticleService {

    @Autowired
    NewKnowledgeService knowledgeService;

    /**
     * 标准热门文章的样子
     * {
     *     播放量: 100000,                     25%权重
     *     点赞量: 8000,                       40%权重
     *     收藏量: 2000,                       15%权重
     *     发布时间: 距离当前时间的天数(越近越好),   20%权重
     * }
     */
    @Override
    public List<Long> getHotArticleId(List<NewKnowledge> articles,Integer wants) {
        return articles.stream()
                .map(article -> {
                    double viewRank = (double) article.getViewCount() / 100000 * 0.25;
                    double likeRank = (double) article.getLikeCount() / 100000 * 0.4;
                    double collectRank = (double) article.getCollectionCount() / 100000 * 0.15;
                    double publishRank = (double) (System.currentTimeMillis() - article.getCreateTime().toEpochSecond(ZoneOffset.UTC)) / 100000 * 0.2;
                    double score = viewRank + likeRank + collectRank + publishRank;
                    return new RankItem(article.getId(), score);
                }).sorted(Comparator.comparingDouble(RankItem::getScore).reversed())
                .limit(wants)
                .map(RankItem::getId)
                .toList();

    }

    @Override
    public boolean support(String type) {
        return "default".equals(type);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class RankItem{
    private Long id;
    private double score;
}
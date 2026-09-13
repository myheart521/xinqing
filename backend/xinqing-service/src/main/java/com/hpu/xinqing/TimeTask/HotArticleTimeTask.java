package com.hpu.xinqing.TimeTask;

import com.hpu.xinqing.service.HotArticleService;
import com.hpu.xinqing.service.NewKnowledgeService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.List;

@Component
@Slf4j
public class HotArticleTimeTask {

    @Autowired
    List<HotArticleService> services;
    @Autowired
    NewKnowledgeService newKnowledgeService;
    @Autowired
    RedisTemplate redisTemplate;




    HotArticleService service;

    //热点文章推荐的服务的名字
    @Value("${algorithm.hotArticle.name}")
    private String serviceName="default";

    //想要几个热点文章
    @Value("${algorithm.hotArticle.wants}")
    private Integer wants=5;



    @PostConstruct
    public void init(){
        service = runWho(serviceName);
        handleService();
    }


//    @Scheduled(cron = "0 */1 * * * ?") // 每分钟执行一次，方便测试
    @Scheduled(cron = "0 0 0 * * ?")
    public void run(){
        handleService();
    }



    private void handleService() {
        log.info("刷新一次热点文章推荐");
        toRedis(service.getHotArticleId(newKnowledgeService.getAll(),wants));
    }


    private void toRedis(List<Long> hotArticleId) {
        int size = redisTemplate.opsForList().range(RedisConstant.HOT_ARTICLE_NEW_KNOWLEDGE, 0, -1).size();
        if(size>0){
            //删除redis中旧的数据
            redisTemplate.opsForList().trim(RedisConstant.HOT_ARTICLE_NEW_KNOWLEDGE,1,0);
        }
        redisTemplate.opsForList().leftPushAll(RedisConstant.HOT_ARTICLE_NEW_KNOWLEDGE,hotArticleId);
    }

    private HotArticleService runWho(String serviceName){
        HotArticleService defaultService = null;
        for (HotArticleService service : services) {
            if (service.support("default")) defaultService = service;
            if(service.support(serviceName)){
                return service;
            }
        }
        log.info("正在使用默认hotArticle推荐服务");
        return defaultService;
    }


}

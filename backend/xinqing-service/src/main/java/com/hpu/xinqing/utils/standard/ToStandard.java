package com.hpu.xinqing.utils.standard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpu.xinqing.config.ObjMapperConfig;
import com.hpu.xinqing.mapper.NewKnowledgeMapper;
import com.hpu.xinqing.mapper.SportMapper;
import com.hpu.xinqing.mapper.UserMapper;
import com.hpu.xinqing.service.NewKnowledgeService;
import com.hpu.xinqing.service.SportService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.utils.StringUtils;
import com.hpu.xinqingpojo.VO.HotArticleVO;
import com.hpu.xinqingpojo.entity.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ToStandard {
    @Autowired
    @Qualifier(value = ObjMapperConfig.OBJECT_MAPPER)
    ObjectMapper objectMapper;

    @Autowired
    SportMapper sportMapper;

    @Autowired
    UserService userService;
    @Autowired
    NewKnowledgeService newKnowledgeService;
    @Resource
    UserMapper userMapper;

    // --------------------------Banner表
    public Map<String,Object> standard(Banner banner){
        return new StandardChain(banner)
                .get();
    }

    public List<Map<String,Object>> banners(List<Banner> banners){
        return banners.stream()
                .map(this::standard)
                .toList();
    }

    //-------------------------Swiper表
    public Map<String,Object> standard(Swiper swiper){
        Map<String,Object> map = objectMapper.convertValue(swiper, new TypeReference<Map<String, Object>>() {
        });
        return new StandardChain(map)
                .get();
    }

    public List<Map<String,Object>> swipers(List<Swiper> swipers){
        return swipers.stream()
                .map(this::standard)
                .toList();
    }

    //---------------------User表
    public Map<String,Object> standard(User user){
        return new StandardChain(user)
                .removeLogicDelete()
                .get();
    }

    public List<Map<String,Object>> users(List<User> users){
        return users.stream()
               .map(this::standard)
               .toList();
    }

    public Map<String,Object> userInfo(User user){
//        System.out.println("user = " + user);
        return StandardChain.getInstance(user,User::getUserAvatar,
                User::getUserProfile,
                User::getSchool,
                User::getPhone,
                User::getSex,
                User::getProvince)
                .get();

//        return Map.of("userAvatar",user.getUserAvatar(),
//                "userProfile",user.getUserProfile(),
//                "school",user.getSchool(),
//                "phone",user.getPhone(),
//                "sex",user.getSex(),
//                "province",user.getProvince());
    }

    //-----------------------Sport表
    public Map<String,Object> standard(Sport sport){
        return new StandardChain(sport)
                .remove(Sport::getText,Sport::getVideo,Sport::getTime)
               .get();
    }



    //-------------------userAndSport表
    public Map<String,Object> standard(UserAndSport userAndSport){
        return StandardChain.getInstance(userAndSport,UserAndSport::getCreateTime)
                .concat(standard(sportMapper.selectSportById(userAndSport.getSportId())))
               .get();
    }

    public List<Map<String,Object>> userAndSports(List<UserAndSport> userAndSports){
        return userAndSports.stream()
               .map(this::standard)
               .toList();
    }

    /**
     *{
     *     "2024-05-01":{
     *         "篮球":2,
     *         "足球":1
     *     },
     *     "2024-05-02":{
     *         "篮球":2,
     *         "足球":1
     *     }
     *}
     */
    public Map<LocalDateTime,Map<String,Object>> userAndSportGraph(List<UserAndSport> userAndSports){
        Map<LocalDateTime,Map<String,Object>> res=new HashMap<>();
        userAndSports.forEach(userAndSport -> userAndSport.setCreateTime(userAndSport.getCreateTime().toLocalDate().atStartOfDay()));
        Map<LocalDateTime, List<UserAndSport>> collect = userAndSports.stream().collect(Collectors.groupingBy(UserAndSport::getCreateTime));
        collect.keySet().forEach(dt->{
                    Map<String,Object> elem = new HashMap<>();
                    Map<Long, List<UserAndSport>> map = collect.get(dt).stream().collect(Collectors.groupingBy(UserAndSport::getSportId));
                    map.keySet().forEach(sportId->{
                        int times = map.get(sportId).size();
                        String title = sportMapper.selectSportById(sportId).getTitle();
                        elem.put(title,times);
                    });
                    res.put(dt,elem);
                });
        return res;
    }

    //-------------------Feedback表
    public Map<String,Object> standard(Feedback feedback){
        return new StandardChain(feedback)
                .remove("userId","images")
               .get();
    }


    public Map<String,Object> standardFeedback(PageResult<Feedback> page) {
        Map<String ,Object> map=new HashMap<>();
        map.put("total",page.getTotal());
        List<Feedback> records = page.getRecords();
        List<Map<String, Object>> list = records.stream().map(this::standard)
                .toList();
        map.put("records",list);
        return map;
    }

    public Map<String,Object> standardFeedbackVO(Feedback feedback) {
        return new StandardChain(feedback)
                .remove("userId","images")
                .put("imageList", StringUtils.imageToList(feedback.getImages()))
                .put("userName", userService.getById(feedback.getUserId()).getUserName())
                .get();
    }


    //-----------------------newKnowledge表
    public HotArticleVO hotArticle(NewKnowledge newKnowledge){
        User user = userMapper.selectById(newKnowledge.getCreateId());
        return new StandardChain(newKnowledge)
                .remove(NewKnowledge::getContent)
                .put(user,User::getUserName)//放入创建人昵称
               .get(HotArticleVO.class);
    }

    public List<HotArticleVO> hotArticles(List<NewKnowledge> newKnowledges){
        return newKnowledges.stream()
               .map(this::hotArticle)
               .toList();
    }
}

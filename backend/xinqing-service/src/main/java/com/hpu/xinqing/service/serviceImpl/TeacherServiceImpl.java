package com.hpu.xinqing.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpu.xinqing.mapper.BannerMapper;
import com.hpu.xinqing.mapper.DietMapper;
import com.hpu.xinqing.mapper.NewKnowledgeMapper;
import com.hpu.xinqing.mapper.UserMapper;
import com.hpu.xinqing.service.DietService;
import com.hpu.xinqing.service.NewKnowledgeService;
import com.hpu.xinqing.service.TeacherService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqing.utils.standard.ToStandard;
import com.hpu.xinqingpojo.DTO.BasePageDTO;
import com.hpu.xinqingpojo.DTO.DietDTO;
import com.hpu.xinqingpojo.DTO.NewKnowledgeDTO;
import com.hpu.xinqingpojo.enmus.RoleEnums;
import com.hpu.xinqingpojo.entity.Banner;
import com.hpu.xinqingpojo.entity.Diet;
import com.hpu.xinqingpojo.entity.NewKnowledge;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@Service
public class TeacherServiceImpl implements TeacherService {

    @Resource
    UserMapper userMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Resource
    NewKnowledgeMapper newKnowledgeMapper;
    @Resource
    DietMapper dietMapper;
    @Resource
    BannerMapper bannerMapper;
    @Autowired
    NewKnowledgeService newKnowledgeService;
    @Autowired
    DietService dietService;
    @Autowired
    UserService userService;
    @Autowired
    ToStandard toStandard;





    private Map<String,Function<Long,Map<String,Object>>> funMap;

    private Map<String, BiFunction<Long, BasePageDTO, List<Map>>> articleFunMap;

    @PostConstruct
    public void init() {

        funMap = Map.of("main",getStuMainData);

        articleFunMap=Map.of("article",getNewKnowledgeData,//获取文章
                "diet",getDietData//获取美食
        );
    }


    /**
     * 如果需要返回更多的字段, 请现在funMap中添加一个新的key, 然后写一个Function函数即可
     * @param stuId
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getStuInfo(Long stuId, List<String> params) {
        Map<String,Object> res = new HashMap<>();
        for (String param : params) {
            Function<Long, Map<String, Object>> fun = funMap.get(param);
            if (fun != null) {
                res.putAll(fun.apply(stuId));
            }
        }
        return res;
    }

    @Override
    public  List<Map> getArticle(Long userId,BasePageDTO pageDTO, String params) {
        BiFunction<Long, BasePageDTO, List<Map>> fun = articleFunMap.get(params);
        if (Objects.isNull(fun)) return null;
        return fun.apply(userId, pageDTO);
    }

    @Override
    public List<User> getStuList(Long userId) {
        String school = userMapper.selectById(userId).getSchool();
        return userService.getUserBySchool(school, RoleEnums.STUDENT);


    }

    @Override
    public void publish(NewKnowledgeDTO article) {
        NewKnowledge obj = new NewKnowledge();
        BeanUtils.copyProperties(article,obj);
        newKnowledgeMapper.insert(obj);
    }

    @Override
    public void publishDiet(DietDTO dietDTO) {
        Diet diet = new Diet();
        BeanUtils.copyProperties(dietDTO,diet);
        dietMapper.insert(diet);
    }

    @Override
    public void publishBanner(Banner banner) {
        bannerMapper.insert(banner);
    }




    private final Function<Long,Map<String,Object>> getStuMainData=(userId)-> objectMapper.convertValue(userMapper.selectById(userId),Map.class);


    private final BiFunction<Long,BasePageDTO,List<Map>> getNewKnowledgeData=(userId,pageDTO)->newKnowledgeService.getMapByUserId(userId,pageDTO);
    private final BiFunction<Long,BasePageDTO,List<Map>> getDietData=(userId,pageDTO)->dietService.getMapByUserId(userId,pageDTO);
}

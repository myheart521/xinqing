package com.hpu.xinqing.manage.StudentAgent.tools;


import com.hpu.xinqing.mapper.IntroductionMapper;
import com.hpu.xinqing.mapper.SportMapper;
import com.hpu.xinqing.service.*;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.ModulesPageVO;
import com.hpu.xinqingpojo.VO.SportVO;
import com.hpu.xinqingpojo.entity.*;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;


/**
 * 学生智能体搭建
 */
@Component
public class StudentTools {

    @Resource
    private MusicService musicService;

    @Resource
    private IBlogService blogService;

    @Resource
    private SportService sportService;

    @Resource
    private SportMapper sportMapper;

    @Resource
    private NewKnowledgeService knowledgeService;

    @Resource
    private DietService dietService;

    @Resource
    private ModulesService modulesService;

    @Resource
    private IActivityService activityService;

    @Resource
    private IntroductionMapper introductionMapper;

    @Tool("当用户心理状态非常消极甚至有自杀，自残，危害社会的想法时调用这个工具")
    public void checkStudent(@P("当前会话Id") String memoryId) {
        System.out.println("memoryId");
    }

    @Tool("查找音乐列表，为用户推荐合适的音乐")
    public String findMusic() {
        List<Music> list = musicService.list();
        //将list转为合适的字符串返回
        StringBuffer result = new StringBuffer();
        list.stream().forEach(music -> {
                    String title = music.getTitle();
                    String artist = music.getArtist();
                    //拼接为字符串
                    result.append("音乐名：").append(title).append("，歌手：").append(artist).append("\n");
                }
        );
        return "音乐列表为：" + result.toString();
    }

    @Tool("查找博客列表，为用户推荐博客")
    public String findBlog() {
        List<Blog> list = blogService.list();
        StringBuffer result = new StringBuffer();
        list.stream().forEach(blog -> {
            String title = blog.getTitle();
            String tag = blog.getTag();
            result.append("博客标题：").append(title).append("，标签：").append(tag).append("\n");
        });
        return "博客列表为：" + result.toString();
    }

    @Tool("查找活动列表，为用户推荐活动")
    public String findActivity() {
        List<Activity> list = activityService.list();
        StringBuffer result = new StringBuffer();
        list.stream().forEach(activity -> {
            String title = activity.getTitle();
            String tags = activity.getTag();
            result.append("活动标题：").append(title).append("，标签：").append(tags).append("\n");

        });
        return "活动列表为：" + result.toString();
    }

    @Tool("查找知识科普文章，为用户推荐科普文章")
    public String findKnowledge(){
        List<NewKnowledge> list = knowledgeService.list();
        StringBuffer result = new StringBuffer();
        list.forEach(knowledge -> {
            String title = knowledge.getTitle();
            String description = knowledge.getDescriptions();
            result.append("文章标题：").append(title).append("，文章简介：").append(description).append("\n");
        });
        return "科普文章列表为：" + result.toString();
    }

    @Tool("查找饮食列表，为用户推荐饮食文章")
    public String findDiet() {
        List<Diet> list = dietService.list();
        StringBuffer result = new StringBuffer();
        list.forEach(diet -> {
            String title = diet.getTitle();
            String description = diet.getName();
            result.append("饮食标题：").append(title).append("，菜品名称：").append(description).append("\n");
        });
        return "饮食列表为：" + result.toString();
    }

    @Tool("查找运动视频列表，为用户推荐运动")
    public String findSportVideo() {
        List<SportVO> select = sportService.select(null);
        StringBuffer result = new StringBuffer();
        select.forEach(sport -> {
          String title = sport.getTitle();
          result.append("运动标题：").append(title).append("\n");
        });
        return "运动视频列表为：" + result.toString();
    }

    @Tool("查找测试题库列表，为用户推荐测试")
    public String findWhiteNoise() {
        PageDTO pageDTO = new PageDTO();
        List<ModulesPageVO> testByPages = modulesService.getTestByPages(pageDTO);
        StringBuffer result = new StringBuffer();
        testByPages.forEach(modulesPageVO -> {
            String title = modulesPageVO.getTitle();
            String introductions = modulesPageVO.getIntroductions();
            result.append("测试标题：").append(title).append("，测试简介：").append(introductions).append("\n");
        });
        return "测试题库列表为：" + result.toString();

    }


    @Tool("为学生推荐心理咨询老师")
    public String findPsychologist() {
        List<Doctor> doctors = introductionMapper.findDoctors(1L);
        StringBuffer result = new StringBuffer();
        doctors.forEach(doctor -> {
            String name = doctor.getDoctorName();
            String gender = doctor.getGender();
            //随机生成1-50咨询量
            int consultNum = new Random().nextInt(50) + 1;
            String specializedFields = doctor.getSpecializedFields();
            result.append("心理咨询老师：").append(name).append("，性别：").append(gender).append("，咨询量：").append(consultNum).append("，擅长领域：").append(specializedFields).append("\n");
        });
        return "心理咨询老师列表为：" + result.toString();
    }
}

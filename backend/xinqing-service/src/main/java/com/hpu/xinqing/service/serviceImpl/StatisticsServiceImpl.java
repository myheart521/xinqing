package com.hpu.xinqing.service.serviceImpl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.mapper.*;
import com.hpu.xinqing.service.StatisticsService;
import com.hpu.xinqingpojo.VO.*;
import com.hpu.xinqingpojo.enmus.RoleEnums;

import com.hpu.xinqingpojo.entity.HistoryTest;
import com.hpu.xinqingpojo.entity.Student;
import com.hpu.xinqingpojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private HistoryTestMapper historyTestMapper;
    @Autowired
    private ActivityFollowMapper activityFollowMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Override
    public List<StatsCardVo> getStatsCards() {
        List<StatsCardVo> statsCardVos = new ArrayList<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("role_id", RoleEnums.STUDENT.getType());
        Long studentCount = userMapper.selectCount(queryWrapper);
//        queryWrapper.eq("role_id", RoleEnums.STUDENT.getType());
        statsCardVos.add(new StatsCardVo("学生人数", studentCount));
        queryWrapper.clear();
        queryWrapper.eq("role_id", RoleEnums.TEACHER.getType());
        Long teacherCount = userMapper.selectCount(queryWrapper);
        statsCardVos.add(new StatsCardVo("心理咨询老师人数", teacherCount));
        QueryWrapper<User> studentQueryWrapper = new QueryWrapper<>();
        studentQueryWrapper.lt("score",50);
        studentQueryWrapper.eq("role_id",RoleEnums.STUDENT.getType());
        Long crisisStudent = userMapper.selectCount(studentQueryWrapper);

        statsCardVos.add(new StatsCardVo("心理危机学生人数", crisisStudent));
        studentQueryWrapper.clear();
        studentQueryWrapper.ge("score",50);
        studentQueryWrapper.eq("role_id",RoleEnums.STUDENT.getType());
        Long healthyStudent = userMapper.selectCount(studentQueryWrapper);
        statsCardVos.add(new StatsCardVo("心理健康学生人数", healthyStudent));
        return statsCardVos;
    }

    @Override
    public List<DataVo> getPieData() {
        List<DataVo> piaDataVos = new ArrayList<>();
        QueryWrapper<User> studentQueryWrapper = new QueryWrapper<>();
        studentQueryWrapper.lt("score",60);
        studentQueryWrapper.eq("role_id",RoleEnums.STUDENT.getType());
        Long crisisStudent = userMapper.selectCount(studentQueryWrapper);
        piaDataVos.add(new DataVo("心理危机学生", crisisStudent));
        studentQueryWrapper.clear();
        studentQueryWrapper.ge("score",60);
        studentQueryWrapper.eq("role_id",RoleEnums.STUDENT.getType());
        Long healthyStudent = userMapper.selectCount(studentQueryWrapper);
        piaDataVos.add(new DataVo("心理健康学生", healthyStudent));
        return piaDataVos;
    }

    @Override
    public List<DataVo> getTeacherCommData() {
        List<DataVo> dataVos = chatMapper.selectTopTeachersWithChatCounts();
        return dataVos;
    }

    @Override
    public List<DataVo> getEvaluationData() {
        List<DataVo> dataVos = historyTestMapper.selectTopStudentsByTestCount();
        return dataVos;
    }

    @Override
    public List<DataVo> getPostData() {
        List<DataVo> dataVos = blogMapper.getPostData();
        return dataVos;
    }

    @Override
    public List<DataVo> getActivityData() {
        List<DataVo> dataVos = activityFollowMapper.selectTopParticipants();
        return dataVos;
    }

    @Override
    public LineVo getLine() {
        LineVo lineVo = new LineVo();
        String[] lineX = new String[7];
        Integer[] lineY = new Integer[7];
        //日期统计
        for (int i = lineX.length-1; i >= 0; i--) {
            LocalDate date=LocalDate.now().minusDays(i);
            lineX[i] = date.toString();
//            System.out.println(lineX[i]);
            //每天的咨询数量=教师和学生的沟通次数
            Integer count = chatMapper.selectConsultCount(LocalDate.parse(lineX[i]));
            lineY[i]=count;
        }
        lineVo.setLineX(lineX);
        lineVo.setLineY(lineY);
        return lineVo;
    }
//    {
//    "学习适应能力": 49,
//    "情绪适应能力": 51,
//    "择业适应能力": 64,
//    "校园适应能力": 55,
//    "自我适应能力": 65,
//    }
    @Override
    public RadarVo getRadar() {
        RadarVo radarVo = new RadarVo();
        QueryWrapper<HistoryTest> historyTestQueryWrapper = new QueryWrapper<>();
        historyTestQueryWrapper.eq("modules_id",2);
        List<HistoryTest> historyTests = historyTestMapper.selectList(historyTestQueryWrapper);
        Integer[] sum = {0,0,0,0,0};
        int indeies = historyTests.size();
        String[] ability = {"学习适应能力","情绪适应能力","择业适应能力","校园适应能力","自我适应能力"};
        List radars = new ArrayList<RadarIndicators>();
        for (HistoryTest historyTest:historyTests) {
            JSONObject jsonObject = JSONUtil.parseObj(historyTest.getTestDescription());
            Integer study = (Integer) jsonObject.get(ability[0]);
            sum[0]+=study;
            Integer emotion = (Integer) jsonObject.get(ability[1]);
            sum[1]+=emotion;
            Integer profession = (Integer) jsonObject.get(ability[2]);
            sum[2]+=profession;
            Integer compus = (Integer) jsonObject.get(ability[3]);
            sum[3]+=compus;
            Integer self = (Integer) jsonObject.get(ability[4]);
            sum[4]+=self;
        }
        for (int i = 0; i < sum.length; i++) {
            sum[i]=sum[i]/indeies;
            RadarIndicators radarIndicators = new RadarIndicators();
            radarIndicators.setName(ability[i]);
            radarIndicators.setMax(100);
            radars.add(radarIndicators);
        }
        radarVo.setRadarIndicators(radars);
        radarVo.setRadarData(sum);
        return radarVo;
    }

    @Override
    public List<CollegePointsVo> getCollegePoints() {
        ArrayList<CollegePointsVo> collegePointsVos = new ArrayList<>();

        List<CollegeCountVo> collegeCountVo = userMapper.selectCollegeCount();
        for (int i = 0; i < collegeCountVo.size(); i++) {
            CollegeCountVo collegeCountVo1 = collegeCountVo.get(i);
            CollegePointsVo collegePointsVo = new CollegePointsVo();
            Double[] aDouble = new Double[3];
            aDouble[0]= collegeCountVo1.getLongitude();
            aDouble[1]= collegeCountVo1.getLatitude();
            aDouble[2]= Double.valueOf(collegeCountVo1.getCount());
            collegePointsVo.setName(collegeCountVo1.getName());
            collegePointsVo.setValue(aDouble);
            collegePointsVos.add(collegePointsVo);
        }

        return collegePointsVos;
    }
}

package com.hpu.xinqing.service.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.HistoryTestMapper;
import com.hpu.xinqing.mapper.ModulesMapper;
import com.hpu.xinqing.service.HistoryTestService;
import com.hpu.xinqingcommon.exception.UserNotLoginException;
import com.hpu.xinqingcommon.utils.StpUtils;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.AnswerVO;
import com.hpu.xinqingpojo.VO.HistoryTestPageVO;
import com.hpu.xinqingpojo.VO.HistoryTestReportVO;
import com.hpu.xinqingpojo.entity.HistoryTest;
import com.hpu.xinqingpojo.entity.Modules;
import com.hpu.xinqingpojo.entity.User;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HistoryTestServiceImpl implements HistoryTestService {
    @Autowired
    private HistoryTestMapper historyTestMapper;
    @Autowired
    private ModulesMapper modulesMapper;
    @Autowired
    private TestServiceImpl testService;
    @Override
    public List<HistoryTestPageVO> getHistoryPage(PageDTO pageDTO) {
        Page<HistoryTest> historyTestPage = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());
        OrderItem orderItem = new OrderItem().setAsc(true).setColumn("create_time");
        historyTestPage.addOrder(orderItem);
        List<String> list = List.of(new String[]{"id","modules_id","create_time"});

        Long userId = StpUtils.userId();
        Page<HistoryTest> page = historyTestMapper.selectPage(historyTestPage, new QueryWrapper<HistoryTest>().select(list).eq("user_id", userId));
        List<HistoryTest> records = page.getRecords();
        HashSet moduleIds = new HashSet<Long>();
        for (int i = 0; i<records.size() ; i++ ) {
            moduleIds.add(records.get(i).getModulesId());
        }
        List<Modules> modulesList = modulesMapper.selectBatchIds(moduleIds);
        List<HistoryTestPageVO> historyTestPageVOS = new ArrayList<>();
        for (HistoryTest item:records) {
            for (Modules module:modulesList) {
                if (module.getId().equals(item.getModulesId()))
                {
                    HistoryTestPageVO historyTestPageVO = new HistoryTestPageVO();
                    historyTestPageVO.setCreateTime(item.getCreateTime());
                    historyTestPageVO.setId(item.getId());
                    historyTestPageVO.setTitle(module.getTitle());
                    historyTestPageVO.setSrc(module.getSrc());
                    historyTestPageVOS.add(historyTestPageVO);
                }
            }
        }
        return historyTestPageVOS;
    }

    @Override
    public AnswerVO getDetail(Long id) {
        HistoryTest historyTest = historyTestMapper.selectById(id);
        Long modulesId = historyTest.getModulesId();
        Modules modules = modulesMapper.getById(modulesId);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Double> stringDoubleMap= new HashMap<>();
        try {
            // 使用 Jackson 的 ObjectMapper 将 JSON 字符串解析为 Map
            stringDoubleMap = objectMapper.readValue(historyTest.getTestDescription(), new TypeReference<Map<String, Double>>() {
            });
        } catch (Exception e) {
            // 捕获解析异常并抛出自定义异常
            throw new RuntimeException("JSON 转换为 Map 失败: " + e.getMessage(), e);
        }
        AnswerVO answerVO = testService.getAnswerVO(modules, stringDoubleMap);
        return answerVO;
    }

    @Override
    public  HistoryTestReportVO dataReport(Long moduleId, Long recent) {
        Long userId = StpUtils.userId();
        if(ObjectUtil.isEmpty(userId)){
            throw new UserNotLoginException("未登录，请先登录");
        }

        QueryWrapper<HistoryTest> lastHistoryTest = new QueryWrapper<HistoryTest>().
                eq("user_id",userId).
                eq("modules_id",moduleId).
                orderByDesc("create_time").
                last("LIMIT " + recent);
        List<HistoryTest> historyTests = historyTestMapper.selectList(lastHistoryTest);

        HistoryTestReportVO historyTestReportVO = new HistoryTestReportVO();
        HashMap<String, List<Integer>> yHashMap = new HashMap<>();
        ArrayList<LocalDateTime> localDateTimes = new ArrayList<>();

        for (HistoryTest historyTest :historyTests) {
            localDateTimes.add(historyTest.getCreateTime());
            HashMap<String,Integer> hashMap = JSONUtil.parseObj(historyTest.getTestDescription()).toBean(HashMap.class);
            for (String key:hashMap.keySet()) {
                List<Integer> newScores = yHashMap.getOrDefault(key, new ArrayList<Integer>());
                Integer integer = hashMap.get(key);
                System.out.println(integer);
                newScores.add(integer);
                yHashMap.put(key,newScores);
            }
        }
        historyTestReportVO.setXAxis(localDateTimes);
        historyTestReportVO.setYAxis(yHashMap);
        return historyTestReportVO;
    }

    @Override
    public List<HistoryTest> getHistoryByUserId(Long userId, Integer count) {
        LambdaQueryWrapper<HistoryTest> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HistoryTest::getUserId,userId)
                .orderByDesc(HistoryTest::getCreateTime)
                .last("limit "+count);
        return historyTestMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public Long getHistoryCount(Long userId) {
        return historyTestMapper.selectCount(new LambdaQueryWrapper<HistoryTest>().eq(HistoryTest::getUserId,userId));
    }

    @Override
    public List<HistoryTestReportVO> dataReportByStudentId(Long recent, Long userId) {
        List<HistoryTestReportVO> list=new ArrayList<>();
        List<Modules> allDefault = modulesMapper.getAllDefault();
        for (Modules modules : allDefault) {
            Long moduleId=modules.getId();
            QueryWrapper<HistoryTest> lastHistoryTest = new QueryWrapper<HistoryTest>().
                    eq("user_id",userId).
                    eq("modules_id",moduleId).
                    orderByDesc("create_time").
                    last("LIMIT " + recent);
            List<HistoryTest> historyTests = historyTestMapper.selectList(lastHistoryTest);

            HistoryTestReportVO historyTestReportVO = new HistoryTestReportVO();
            HashMap<String, List<Integer>> yHashMap = new HashMap<>();
            ArrayList<LocalDateTime> localDateTimes = new ArrayList<>();

            for (HistoryTest historyTest :historyTests) {
                localDateTimes.add(historyTest.getCreateTime());
                HashMap<String,Integer> hashMap = JSONUtil.parseObj(historyTest.getTestDescription()).toBean(HashMap.class);
                for (String key:hashMap.keySet()) {
                    List<Integer> newScores = yHashMap.getOrDefault(key, new ArrayList<Integer>());
                    Integer integer = hashMap.get(key);
                    System.out.println(integer);
                    newScores.add(integer);
                    yHashMap.put(key,newScores);
                }
            }
            historyTestReportVO.setXAxis(localDateTimes);
            historyTestReportVO.setYAxis(yHashMap);
            historyTestReportVO.setModuleId(moduleId);
            historyTestReportVO.setTitle(modules.getTitle());
            list.add(historyTestReportVO);
        }
        return list;

    }
}

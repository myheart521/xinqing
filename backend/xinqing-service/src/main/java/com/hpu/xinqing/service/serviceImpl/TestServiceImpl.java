package com.hpu.xinqing.service.serviceImpl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hpu.xinqing.mapper.*;
import com.hpu.xinqing.service.TestService;
import com.hpu.xinqingcommon.exception.BaseException;
import com.hpu.xinqingpojo.DTO.QuestionSubmitDTO;
import com.hpu.xinqingpojo.VO.AnswerVO;
import com.hpu.xinqingpojo.VO.QuestionVO;
import com.hpu.xinqingpojo.entity.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private QuestionsMapper questionsMapper;
    @Autowired
    private ModulesMapper modulesMapper;
    @Autowired
    private PersonalityMapper personalityMapper;
    @Autowired
    private HistoryTestMapper historyTestMapper;
    @Override
    public List<QuestionVO> findAll(Long id) {
        //获取测试题并做数据预处理
        List<Questions> questionsList =  questionsMapper.getByTestId(id);
        List<QuestionVO> questionVOS = new ArrayList<>();
        for (Questions questions:questionsList) {
            QuestionVO questionVO = new QuestionVO();
            BeanUtils.copyProperties(questions, questionVO);
            String[] options = questions.getOptions().split(" ");
            String[] vals = questions.getVal().split(" ");
            questionVO.setOptions(options);
            questionVO.setVal(vals);
            questionVOS.add(questionVO);
        }
        return questionVOS;
    }
    @Transactional
    public AnswerVO calculateAns(QuestionSubmitDTO questionSubmitDTO, Long userId) {
        //确保做完所有题目，防止出现错误
        Modules module = modulesMapper.getById(questionSubmitDTO.getModuleId());
        List<Questions> questions = questionsMapper.getByTestId(module.getId());
        if (questions.size()!=questionSubmitDTO.getAns().length){
            throw new BaseException("请回答完所有问题后再测试！！！");
        }
        //数据预处理，将string类型转化成数字
        String[] ansString = questionSubmitDTO.getAns();
        ArrayList<Integer> ans = new ArrayList<>();
        for (String s : ansString) {
            try{
                ans.add(Integer.parseInt(s));
            }catch (NumberFormatException e) {
                throw new BaseException("ans数组中含有非数字！！");
            }
        }

        //按照不同的个人特质id聚合
        Map<String, List<Questions>> groupedByTypeId = questions.stream()
                .collect(Collectors.groupingBy(Questions::getTypeIds));
        /***
         * 正式开始计算
         */
        Map<Long,Integer> typeIdAndScore = new HashMap<>();
        Map<Long,Integer> typeIdAndTotal = new HashMap<>();

        //首先判断这个模块中，一道题对应几种类型
        //如果是多的，比如mbti中一道题可以对应多个
        if(module.getIsMultiple()){
            //初始化，防止某些题目一直不被做到
            for(Questions item:questions){
                String[] typeIds = item.getTypeIds().split(" ");
                typeIdAndScore.put(Long.valueOf(typeIds[0]),0);
                typeIdAndScore.put(Long.valueOf(typeIds[1]),0);
                typeIdAndTotal.put(Long.valueOf(typeIds[0]),0);
                typeIdAndTotal.put(Long.valueOf(typeIds[1]),0);
            }
            //如果是一道题对应多个类型，那么ans中存的是选中那个类型的下标
            for (String item : groupedByTypeId.keySet()) {
                //题目对应的类型id
                String[] typeIds = item.split(" ");
                //同一类型的全部题目题目
                List<Questions> questions1 = groupedByTypeId.get(item);
                for (int i = 0; i < questions1.size(); i++) {
                    //在这个if分支中，ans中是选中的下标
                    Long typeId = Long.valueOf(typeIds[ans.get(questions1.get(i).getQuestionNumber()-1)]);
                    //表示哪种类型，你选择了多少次
                    typeIdAndScore.put(typeId, typeIdAndScore.get(typeId) + 1);
                    //总分是多少
                    typeIdAndTotal.put(Long.valueOf(typeIds[0]), typeIdAndTotal.get(Long.valueOf(typeIds[0])) + 1);
                    typeIdAndTotal.put(Long.valueOf(typeIds[1]), typeIdAndTotal.get(Long.valueOf(typeIds[1])) + 1);
                }
            }
            //如果有哪种类型一直不被选到
        }else{
            for (String item : groupedByTypeId.keySet()) {
                //对于这一类型题目的得分
                int typeScore = 0;
                //对于这一类型题目的总分
                int typeTotal = 0;
                //同一类型的全部题目题目
                List<Questions> questions1 = groupedByTypeId.get(item);
                for (int i = 0; i<questions1.size() ;i++){
                    //在这个分支中ans是不同类型题目对应的得分，number-1才是对应的下标
                    typeScore+= ans.get(questions1.get(i).getQuestionNumber()-1);
                    //这个题目中哪个值是最大的
                    int max = Arrays.stream(questions1.get(i).getVal().split(" ")).mapToInt(Integer::parseInt).max()
                            .orElseThrow(() -> new BaseException("Empty array"));;
                    typeTotal+=max;
                }
                //表示哪种类型，你得了多少分
                //加入到集合中
                typeIdAndScore.put(Long.valueOf(item),typeScore);
                //总分是多少
                typeIdAndTotal.put(Long.valueOf(item),typeTotal);
            }
        }


        /***
         * 记录做题结果到数据库和返回前端
         */
        //先保存history_test表中内容
        HistoryTest historyTest = new HistoryTest();
        historyTest.setUserId(userId);
        historyTest.setModulesId(module.getId());
        //为了获取主键id
        historyTestMapper.insert(historyTest);
        Long historyTestId = historyTest.getId();
        //都有哪些类型
        List<Personality> personalities = personalityMapper.selectBatchIds(typeIdAndTotal.keySet());
        //函数值和类型的映射
        Map<Long,String> idAndType = new HashMap<>();
        Map<String,Double> typeAndResult = new HashMap<>();
        for (Personality personality: personalities ) {
            idAndType.put(personality.getId(), personality.getType());
        }
        for (Long item : typeIdAndTotal.keySet()) {
            Integer score = typeIdAndScore.get(item);
            Integer total = typeIdAndTotal.get(item);
            double result = Math.round(score*1.0/total*100.0);
            typeAndResult.put(idAndType.get(item),result);
        }
        String jsonStr = JSONUtil.toJsonStr(typeAndResult);
        historyTest.setTestDescription(jsonStr);
        //更新描述
        historyTestMapper.update(historyTest,new UpdateWrapper<HistoryTest>().eq("id",historyTest.getId()));
        AnswerVO answerVO = getAnswerVO(module, typeAndResult);
        return answerVO;
    }

    public  AnswerVO getAnswerVO(Modules module, Map<String, Double> typeAndResult) {
        //封装数据给前端
        AnswerVO answerVO = new AnswerVO();
        answerVO.setImage(module.getResultSrc());
        answerVO.setTypeAndResult(typeAndResult);
        answerVO.setDescription(module.getResultDescription());
        answerVO.setModuleName(module.getTitle()+"结果");
        return answerVO;
    }
}

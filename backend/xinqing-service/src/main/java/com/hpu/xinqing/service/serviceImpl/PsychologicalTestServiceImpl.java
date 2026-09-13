package com.hpu.xinqing.service.serviceImpl;

import cn.hutool.core.util.ReUtil;
import com.hpu.xinqing.mapper.PsychologicalTestMapper;
import com.hpu.xinqing.service.PsychologicalTestService;
import com.hpu.xinqingpojo.entity.PsychologicalTest;
import com.hpu.xinqingpojo.VO.TestVO;
import com.hpu.xinqingcommon.constant.RegexConstant;
import com.hpu.xinqingcommon.exception.StringInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PsychologicalTestServiceImpl implements PsychologicalTestService {

    private static ArrayList<String> good = new ArrayList<>();
    private static ArrayList<String> bad = new ArrayList<>();
    @Autowired
    PsychologicalTestMapper psychologicalTestMapper;
    @Override
    public List<PsychologicalTest> findAll() {
        return psychologicalTestMapper.selectList(null);
    }

    @Override
    public TestVO calculateAns(String ans) {
        if(!ReUtil.isMatch(RegexConstant.ADAPTATION,ans)){
            throw new StringInvalidException("非法字符串");
        }
        char[] all =ans.toCharArray();
        int[] shift = new int[60];
        int[] relation = {11,13,18,20,35,7,43,50,55,56};
        int[] study = {4,9,24,28,30,34,40,44,47,49,59};
        int[] campus = {17,22,26,32,38,46,54,60};
        int[] job = {5,8,12,14,19,36,41,51,57};
        int[] emo = {1,3,15,21,27,39,45,53,58};
        int[] self = {6,10,16,23,25,29,33,42};
        int[] sat = {2,7,31,48,52};
        int[] change = {5,6,12,18,19,20,23,26,28,30,32,38,40,42,45,48,53,55,57};
        double sum=0.0;
        for(int i = 0,j = 0;i<all.length;i++)
        {
            shift[i]=all[i]-'0';
            if(j<change.length&&i==(change[j]-1)) {
                j++;
                shift[i] = 6 - shift[i];
            }
            sum+=shift[i];
        }
        sum*=(1.0/3);
        int f = (int) Math.round(sum);
        TestVO testVO = new TestVO();
        if(f>=51) testVO.setJudge("适应能力总体较好");
        else testVO.setJudge("适应能力总体有待提高");
        testVO.setRelationship(calculate(relation,shift,"人际关系适应能力"));
        testVO.setStudy(calculate(study,shift,"学习适应能力"));
        testVO.setCampus(calculate(campus,shift,"校园适应能力"));
        testVO.setJob(calculate(job,shift,"择业适应能力"));
        testVO.setEmotion(calculate(emo,shift,"情绪适应能力"));
        testVO.setSelf(calculate(self,shift,"自我适应能力"));
        testVO.setSatisfaction(calculate(sat,shift,"满意度"));
        testVO.setTotal(String.valueOf(f));
        String[] s1 =good.toArray(new String[good.size()]);
        testVO.setGood(s1);
        good.clear();
        String[] s2 = bad.toArray(new String[bad.size()]);
        testVO.setBad(s2);
        bad.clear();
        return testVO;
    }
    private static String calculate(int[] indexs,int[] res,String signature)
    {
        double sum=0;
        for (int j : indexs) sum += res[j - 1];
        double ta = 5*indexs.length;
        sum *= 100/ta;
        int f = (int) Math.round(sum);
        if(f>=51)good.add(signature);
        else bad.add(signature);
        return Integer.toString(f);
    }

}

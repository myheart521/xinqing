package com.hpu.xinqing.service.serviceImpl;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.MbtiResultMapper;
import com.hpu.xinqing.mapper.MbtiTopicMapper;
import com.hpu.xinqing.service.MbtiService;
import com.hpu.xinqingpojo.entity.MbtiResult;
import com.hpu.xinqingpojo.entity.MbtiTopic;
import com.hpu.xinqingpojo.VO.MbtiTestVO;
import com.hpu.xinqingcommon.constant.RegexConstant;
import com.hpu.xinqingcommon.exception.StringInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

@Service
public class MbtiServiceImpl extends ServiceImpl<MbtiTopicMapper,MbtiTopic> implements MbtiService {
    @Autowired
    private MbtiResultMapper resultMapper;

    //查全部
    @Override
    public List<MbtiTopic> findAll() {
        return query().list();
    }

    //计算
    public MbtiTestVO calculateAns(String string) {
        if(!ReUtil.isMatch(RegexConstant.MBTI,string)){
        throw new StringInvalidException("非法字符串");
        }
        StringBuffer stringBuffer = new StringBuffer(string);
        HashMap<Character, Integer> map = new HashMap<>();

        for (int i = 0; i < stringBuffer.length(); i++) {
            char a = stringBuffer.charAt(i);
            if (map.get(a) == null) {
                map.put(a, 1);
            } else {
                map.put(a, map.get(a) + 1);
            }
        }
        System.out.println(map.keySet());
        System.out.println(map.values());

        Integer e = (map.get('E') == null ? 0 : map.get('E'));
        Integer i = (map.get('I') == null ? 0 : map.get('I'));
        Integer s = (map.get('S') == null ? 0 : map.get('S'));
        Integer n = (map.get('N') == null ? 0 : map.get('N'));
        Integer t = (map.get('T') == null ? 0 : map.get('T'));
        Integer f = (map.get('F') == null ? 0 : map.get('F'));
        Integer j = (map.get('J') == null ? 0 : map.get('J'));
        Integer p = (map.get('P') == null ? 0 : map.get('P'));


        //计算比率
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String eRate = decimalFormat.format(((double) e / stringBuffer.length()) * 100);
        String iRate = decimalFormat.format(((double) i / stringBuffer.length()) * 100);
        String sRate = decimalFormat.format(((double) s / stringBuffer.length()) * 100);
        String nRate = decimalFormat.format(((double) n / stringBuffer.length()) * 100);
        String tRate = decimalFormat.format(((double) t / stringBuffer.length()) * 100);
        String fRate = decimalFormat.format(((double) f / stringBuffer.length()) * 100);
        String jRate = decimalFormat.format(((double) j / stringBuffer.length()) * 100);
        String pRate = decimalFormat.format(((double) p / stringBuffer.length()) * 100);

        //拼接结果
        StringBuffer res = new StringBuffer();
        res = e > i ? res.append("E") : res.append('I');
        res = s > n ? res.append('S') : res.append('N');
        res = t > f ? res.append('T') : res.append('F');
        res = j > p ? res.append('J') : res.append('P');

        QueryWrapper<MbtiResult> mbtiResultQueryWrapper = new QueryWrapper<>();
        MbtiResult mbtiResult = resultMapper.selectOne(mbtiResultQueryWrapper.eq("ans_code",res.toString()));
        MbtiTestVO ans = new MbtiTestVO(res.toString(), eRate, iRate, sRate, nRate, tRate, fRate, jRate, pRate, mbtiResult.getAnsName(), mbtiResult.getAnsDisc());

        return ans;
    }
}

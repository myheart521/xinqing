package com.hpu.xinqing.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.mapper.SportImageMapper;
import com.hpu.xinqing.mapper.SportMapper;
import com.hpu.xinqing.service.SportService;
import com.hpu.xinqingpojo.DTO.SportDTO;
import com.hpu.xinqingpojo.VO.SportVO;
import com.hpu.xinqingpojo.VO.SportVideoVO;
import com.hpu.xinqingpojo.entity.Sport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SportServiceImpl implements SportService {
    @Autowired
    SportMapper sportMapper;
    @Autowired
    SportImageMapper sportImageMapper;



    /**
     * 运动基本信息查询
     * @param sportDTO
     * @return
     */
    @Override
    public List<SportVO> select(SportDTO sportDTO) {
        if(sportDTO.getStartTime()==null || sportDTO.getStartTime()<0){
            sportDTO.setStartTime(0);
        }
        if(sportDTO.getEndTime()==null || sportDTO.getEndTime()>100){
            sportDTO.setEndTime(100);
        }
        if(sportDTO.getLeague()!=null && sportDTO.getLeague()!=""){
            Long leagueId=(long)(sportDTO.getLeague().charAt(1)-'0');
            sportDTO.setLeagueId(leagueId);
        }
        List<SportVO> list=sportMapper.select(sportDTO);
        for (SportVO s:list) {
            String[] str= s.getMotion().split("-");
            List<String> motionlist=new ArrayList<>();
            for (int i = 0; i < str.length; i++) {
                motionlist.add(str[i]);
            }
            s.setMotionFeatures(motionlist);
        }
        return list;
    }

    /**
     * 详细信息查询
     * @param id
     * @return
     */
    @Override
    public SportVideoVO selectById(Long id) {
        SportVideoVO sportVideoVO=sportMapper.selectById(id);
        List<String> imageList=sportImageMapper.selectBySportId(id);
        sportVideoVO.setImageList(imageList);
        String[] str= sportVideoVO.getMotion().split("-");
        List<String> motionlist=new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            motionlist.add(str[i]);
        }
        sportVideoVO.setMotionFeatures(motionlist);
        return sportVideoVO;
    }
}

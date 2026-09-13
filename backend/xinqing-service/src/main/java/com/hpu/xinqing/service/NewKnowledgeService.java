package com.hpu.xinqing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.DTO.BasePageDTO;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.NewKnowledgeInfoVo;
import com.hpu.xinqingpojo.VO.NewKnowledgeListVO;
import com.hpu.xinqingpojo.entity.NewKnowledge;

import java.util.List;
import java.util.Map;

public interface NewKnowledgeService extends IService<NewKnowledge> {
    List<NewKnowledgeListVO> pageQuery(PageDTO pageDTO);

    NewKnowledgeInfoVo view(Integer id);

    void like(Integer id);
    void collection(Integer id);
    List<NewKnowledgeListVO> pageCollectionQuery(PageDTO pageDTO);



    //--------------如下是DAO
    List<NewKnowledge> getByUserId(Long userId, BasePageDTO basePageDTO);
    List<Map> getMapByUserId(Long userId, BasePageDTO basePageDTO);
    List<NewKnowledge> getAll();
    List<NewKnowledge> getListByIds(List<Long> ids);
    List<Long> getAllIds();
}

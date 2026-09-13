package com.hpu.xinqing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hpu.xinqingpojo.VO.TempEmotionRecordsVO;
import com.hpu.xinqingpojo.entity.TempEmotionRecords;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @description 针对表【temp_emotion_records】的数据库操作Service
* @createDate 2025-06-09 16:47:21
*/
public interface TempEmotionRecordsService extends IService<TempEmotionRecords> {

    IPage<TempEmotionRecordsVO> getRecordsByPage(int pageNum, int pageSize,Long studentId);
}

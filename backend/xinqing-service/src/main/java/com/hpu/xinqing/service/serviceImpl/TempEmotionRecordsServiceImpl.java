package com.hpu.xinqing.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.TempEmotionRecordsMapper;
import com.hpu.xinqing.service.IActivityService;
import com.hpu.xinqing.service.IBlogService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingpojo.DTO.ActiveAnalyseDTO;
import com.hpu.xinqingpojo.VO.ActivityVO;
import com.hpu.xinqingpojo.VO.BlogVO;
import com.hpu.xinqingpojo.VO.TempEmotionRecordsVO;
import com.hpu.xinqingpojo.VO.UserVO;
import com.hpu.xinqingpojo.entity.Activity;
import com.hpu.xinqingpojo.entity.TempEmotionRecords;
import com.hpu.xinqing.service.TempEmotionRecordsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 针对表【temp_emotion_records】的数据库操作Service实现
 * @createDate 2025-06-09 16:47:21
 */
@Service
public class TempEmotionRecordsServiceImpl extends ServiceImpl<TempEmotionRecordsMapper, TempEmotionRecords>
        implements TempEmotionRecordsService {

    private final UserService userService;
    private final IBlogService blogService;
    private final IActivityService activityService;


    public TempEmotionRecordsServiceImpl(UserService userService, IBlogService blogService, IActivityService activityService) {
        this.userService = userService;
        this.blogService = blogService;
        this.activityService = activityService;
    }

    @Override
    public IPage<TempEmotionRecordsVO> getRecordsByPage(int pageNum, int pageSize,Long studentId) {

        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
        // 创建分页对象
        Page<TempEmotionRecords> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TempEmotionRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TempEmotionRecords::getUserId,studentId)
                .orderByDesc(TempEmotionRecords::getDetectionTime);
        page = this.page(page, queryWrapper);
        List<TempEmotionRecordsVO> tempEmotionRecordsVOS = new ArrayList<>();
        for (TempEmotionRecords record : page.getRecords()) {
            TempEmotionRecordsVO tempEmotionRecordsVO = BeanUtil.copyProperties(record, TempEmotionRecordsVO.class);
            UserVO userVOById = userService.getUserVOById(record.getUserId());
            if (userVOById != null) {
                tempEmotionRecordsVO.setStudent(userVOById);
            }
            BlogVO blogVOById = blogService.getBlogVOById(record.getFeatureBlogId());
            if (blogVOById != null) {
                tempEmotionRecordsVO.setFeatureBlog(blogVOById);
            }
            Activity activityServiceById = activityService.getById(record.getFeatureActiveId());
            if (activityServiceById != null) {
                ActiveAnalyseDTO analyseDTO = BeanUtil.copyProperties(activityServiceById, ActiveAnalyseDTO.class);
                analyseDTO.setTime(activityServiceById.getCreateTime());
                tempEmotionRecordsVO.setFeatureActive(analyseDTO);
            }
            tempEmotionRecordsVOS.add(tempEmotionRecordsVO);
        }
        Page<TempEmotionRecordsVO> pageResult = new Page<>(pageNum, pageSize);
        pageResult.setRecords(tempEmotionRecordsVOS);
        pageResult.setTotal(page.getTotal());
        // 执行分页查询
        return pageResult;

    }
}





package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.VO.SleepAudiosVO;
import com.hpu.xinqingpojo.entity.SleepAudios;

import java.util.List;

/**
 * <p>
 * 助眠 服务类
 * </p>
 *
 * @since 2025-03-15
 */
public interface ISleepAudiosService extends IService<SleepAudios> {

    List<SleepAudiosVO> selectAll();
}

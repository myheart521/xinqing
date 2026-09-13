package com.hpu.xinqing.service.serviceImpl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.AppTimeMapper;
import com.hpu.xinqing.service.IAppTimeService;
import com.hpu.xinqingpojo.entity.AppTime;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 预约时间表 服务实现类
 * </p>
 *
 * @since 2025-07-12
 */
@Service
public class AppTimeServiceImpl extends ServiceImpl<AppTimeMapper, AppTime> implements IAppTimeService {

}

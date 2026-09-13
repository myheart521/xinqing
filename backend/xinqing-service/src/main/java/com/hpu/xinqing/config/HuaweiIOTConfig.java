package com.hpu.xinqing.config;

import com.hpu.xinqing.properties.AliOssProperties;
import com.hpu.xinqing.properties.HuaweiIOTProperties;
import com.hpu.xinqingcommon.utils.AliOssUtil;
import com.hpu.xinqingcommon.utils.HuaweiIOTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration//只有加此注解，下面才能用Bean注解
@Slf4j
public class HuaweiIOTConfig {

//    private String tokenUrl ;//获取token的url
//    private String username ;
//    private String password ;
//    private String IAMAccount ; //IAM用户所属账号名
//    private String endpoint;
//    private String projectId;
//    private String deviceId;
    @Bean
    @ConditionalOnMissingBean//保护整个Spring容器里只有一个HuaweiUtil对象
    public HuaweiIOTUtil huaweiIOTUtil(HuaweiIOTProperties huaweiIOTProperties) {
        log.info("开始创建华为物联网连接对象：{}", huaweiIOTProperties);
        return new HuaweiIOTUtil(huaweiIOTProperties.getTokenUrl(),
                huaweiIOTProperties.getUsername(),
                huaweiIOTProperties.getPassword(),
                huaweiIOTProperties.getIAMAccount(),
                huaweiIOTProperties.getEndpoint(),
                huaweiIOTProperties.getProjectId(),
                huaweiIOTProperties.getDeviceId()
        );}

    }

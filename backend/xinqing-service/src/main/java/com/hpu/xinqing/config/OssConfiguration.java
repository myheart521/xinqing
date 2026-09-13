package com.hpu.xinqing.config;

import com.hpu.xinqing.properties.AliOssProperties;
import com.hpu.xinqingcommon.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类、用于创建AliOssUtil对象
 * */
@Configuration//只有加此注解，下面才能用Bean注解
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean//保护整个Spring容器里只有一个AliOssUtil对象
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties)
    {
    log.info("开始创建阿里云文件上传工具类对象：{}",aliOssProperties);
    return new AliOssUtil(
      aliOssProperties.getEndpoint(),
      aliOssProperties.getAccessKeyId(),
      aliOssProperties.getAccessKeySecret(),
      aliOssProperties.getBucketName());
    }

}

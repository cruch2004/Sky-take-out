package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类 用于创建AliOssUtils对象
 */

@Configuration
@Slf4j
public class OssConfiguration {

    // 创建方法将Utils对象创建出来

    // 现在这个方法是不能被创建的,需要加上@Bean注解

    // 当项目启动时程序会自动调用这个方法将aliOssUtil对象创建出来 交给spring ioc容器进行管理
    @Bean
    @ConditionalOnMissingBean // 保证该对象只被创建一次,当该对象已经被创建成功后就不会再次被创建
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云文件上传工具类对象: {}", aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}

package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {


    // 依赖中引入了RedisConnectionFactory对象 项目会自动创建这个连接工厂对象
    // 并且放到spring容器中 只需要声明下就可以注入进来即可

    // RedisTemplate Redis模板对象
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        log.info("开始创建Redis模板对象");
        RedisTemplate redisTemplate = new RedisTemplate<>();

        // 设置redis的连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置redis key的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        return redisTemplate;
    }


}

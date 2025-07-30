package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);

    /**
     * 根据 user_id查询用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{user_id}")
    User getById(Long userId);

    /**
     * 查询某个时间范围内的用户数
     * @param map
     * @return
     */
    Integer countUserNumberByMap(Map map);
}

package com.leyou.user.service.impl;

import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public Boolean checkData(String data, Integer type) {
        return null;
    }

    @Override
    public void sendVerifyCode(String phone) {

    }

    @Override
    public void register(User user, String code) {

    }

    @Override
    public User queryUser(String userName, String password) {
        return null;
    }
}

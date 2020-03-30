package com.leyou.user.service;

import com.leyou.user.pojo.User;

public interface UserService {
    public Boolean checkData(String data,Integer type);

    public void sendVerifyCode(String phone);

    public void register(User user,String code);

    public User queryUser(String userName,String password);
}

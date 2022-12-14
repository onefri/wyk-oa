package com.example.emos.wx.service;

import com.example.emos.wx.db.pojo.TbUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public interface UserService {


   //注册用户
    Integer registerUser(String registerCode,String code,String nickname,String photo);

    //查询权限
    Set<String> searchUserPermissions(int userId);

    //登录用户
    Integer login(String code);

    TbUser searchById(int userId);


    String searchUserHiredate(int userId);

    HashMap searchUserSummary(int userId);





}

package com.example.emos.wx.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.wx.db.dao.TbCheckinMapper;
import com.example.emos.wx.db.dao.TbUserMapper;
import com.example.emos.wx.db.pojo.TbUser;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.Set;


@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;


    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;



    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        String response = HttpUtil.post(url, map);
        //转换成json字符串
        JSONObject json = JSONUtil.parseObj(response);
        String openId = json.getStr("openid");
        if (openId == null || openId.length() == 0) {
            throw new RuntimeException("临时登陆凭证错误");
        }
        return openId;
    }


    @Override
    //返回主键id
    public int registerUser(String registerCode, String code, String nickname, String photo) {
        if (registerCode.equals("000000")) {
            boolean b = userMapper.haveRootUser();
            if (!b) {
                //把当前用户绑定到ROOT帐户
                String openId = getOpenId(code);
                HashMap param = new HashMap();
                param.put("openId", openId);
                param.put("nickname", nickname);
                param.put("photo", photo);
                param.put("role", "[0]");
                param.put("status", 1);
                param.put("createTime", new Date());
                param.put("root", true);
                userMapper.insert(param);
                int id = userMapper.searchIdByOpenId(openId);
                return id;
            } else {
                throw new EmosException("无法绑定超级管理员！");
            }
            //TODO 此处还有其他判断内容
        } else {
            return 0;
        }


    }

    @Override
    public Set<String> searchUserPermissions(int userId) {
        return userMapper.searchUserPermissions(userId);
    }

    @Override
    public Integer login(String code) {
        String openId = getOpenId(code);
        Integer id = userMapper.searchIdByOpenId(openId);
        if (id == null) {
            throw new EmosException("帐户不存在");
        }
        //TODO 从消息队列中接收消息，转移到消息表
        return id;
    }

    @Override
    public TbUser searchById(int userId) {
        return userMapper.searchById(userId);
    }

    @Override
    public String searchUserHiredate(int userId) {
        return userMapper.searchUserHiredate(userId);
    }

    @Override
    public HashMap searchUserSummary(int userId) {
        return userMapper.searchUserSummary(userId);
    }



}

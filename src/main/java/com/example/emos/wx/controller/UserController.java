package com.example.emos.wx.controller;

import cn.hutool.core.util.IdUtil;
import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.LoginForm;
import com.example.emos.wx.controller.form.RegisterForm;
import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.service.UserService;
import com.example.emos.wx.task.MessageTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Api("用户模块Web接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;


    @Autowired
    private MessageTask messageTask;

    @PostMapping("/register")
    @ApiOperation("注册用户")
    public R register(@Valid @RequestBody RegisterForm form) {
        int id = userService.registerUser(form.getRegisterCode(), form.getCode(), form.getNickname(), form.getPhoto());
        String token = jwtUtil.createToken(id);
        Set<String> permsSet = userService.searchUserPermissions(id);
        saveCacheToken(token, id);

        return R.ok("用户注册成功").put("token", token).put("permission", permsSet);
    }

    private void saveCacheToken(String token, int userId) {
        redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
    }

    @PostMapping("/login")
    @ApiOperation("登录系统")
    public R login(@Valid @RequestBody LoginForm form) {

        Integer userId =  userService.login(form.getCode());

        String token = jwtUtil.createToken(userId);

        Set<String> permsSet = userService.searchUserPermissions(userId);

        saveCacheToken(token, userId);

        return R.ok("登陆成功").put("token", token).put("permission", permsSet);
    }


        @GetMapping("/searchUserSummary")
        @ApiOperation("查询用户摘要信息")
        public R searchUserSummary(@RequestHeader("token") String token) {
            int userId = jwtUtil.getUserId(token);
            HashMap map = userService.searchUserSummary(userId);
            return R.ok().put("result", map);
        }

}


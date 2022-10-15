package com.example.emos.wx.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.CheckinForm;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.CheckinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

@RestController
@RequestMapping("/checkin")
@Api("签到模块Web接口")
@Slf4j
public class CheckinController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CheckinService checkinService;

    @Value("${emos.image-folder}")
    private String imageFolder;

    @GetMapping("/validCanCheckIn")
    @ApiOperation("查询用户是否可以签到")
    public R validCanCheckIn(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);

        String result = checkinService.haveCheckin(userId, DateUtil.today());

        return R.ok(result);
    }

    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@Valid CheckinForm form, @RequestParam("photo") MultipartFile file, @RequestHeader("token") String token) {
        if (null == file) {
            return R.error("没有上传文件");
        }
        //根据token得到id
        int userId = jwtUtil.getUserId(token);
        //转成小写
        String fileName = file.getOriginalFilename().toLowerCase();
        //保存的图片路径
        String path = imageFolder + "/" + fileName;
        //判断是否是JPG格式
        if (!fileName.endsWith(".jpg")) {
            //删除文件路径
            FileUtil.del(path);
            //返回
            return R.error("必须提交JPG格式图片");
        } else {
            try {
                //保存文件路径
                file.transferTo(Paths.get(path));

                checkinService.checkin(form, userId, path);
                return R.ok("签到成功");
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new EmosException("保存图片错误");
            } finally {
                //删除路径
                FileUtil.del(path);
            }
        }
    }

    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo") MultipartFile file, @RequestHeader("token") String token) {

        if (file == null) {
            throw new EmosException("上传文件不能为空！");
        }

        int userId = jwtUtil.getUserId(token);
        String filename = file.getOriginalFilename().toLowerCase();

        String path = imageFolder + "/" + filename;

        if (!filename.endsWith(".jpg")) {
            throw new EmosException("文件格式错误！");
        }

        try {
            file.transferTo(Paths.get(path));
            checkinService.createFaceModel(userId,path);
            return R.ok("上传文件成功！");
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new EmosException("保存文件错误！");
        } finally {
            FileUtil.del(path);
        }
    }


}
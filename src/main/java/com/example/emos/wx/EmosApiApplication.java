package com.example.emos.wx;

import cn.hutool.core.util.StrUtil;
import com.example.emos.wx.db.constants.SystemConstants;
import com.example.emos.wx.db.dao.SysConfigMapper;
import com.example.emos.wx.db.pojo.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

@SpringBootApplication
@ServletComponentScan
@Slf4j
@EnableAsync
public class EmosApiApplication {


    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private SystemConstants constants;

    @Value("${emos.image-folder}")
    private String imageFolder;

    public static void main(String[] args) {
        SpringApplication.run(EmosApiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        List<SysConfig> list = sysConfigMapper.selectAllParam();
        for (SysConfig config : list) {
            //取出常量的名字
            String key = config.getParamKey();
            //转换成驼峰
            key = StrUtil.toCamelCase(key);
            String value = config.getParamValue();
            try {
                //根据常量的名字找到变量赋值
                Field field = constants.getClass().getDeclaredField(key);
                field.set(constants,value);
            } catch (Exception e) {
             log.error("执行异常",e);
            }
        }


        //如果文件不存在就创建
        new File(imageFolder).mkdirs();
    }
}


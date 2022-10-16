package com.example.emos.wx;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.emos.wx.exception.EmosException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Date;

@SpringBootTest
class EmosApiApplicationTests {

    @Test
    void contextLoads() {
        String e = "1234567";
        String s = e.split("2")[1];
        System.out.println(s);
    }

    @Test
    void test1() {
        String path = "wewewewe";
        if (!path.endsWith(".jpg")) {
            throw new RuntimeException("报错！");
        }
    }

    @Test
    void test2() {

        DateTime dateTime = DateUtil.beginOfWeek(DateUtil.date());
        DateTime endOfWeek = DateUtil.endOfWeek(DateUtil.date());

        System.out.println(dateTime);
        System.out.println(endOfWeek);
    }

    @Test
    void test3() {

     throw new EmosException("异常");
    }


}



package com.example.emos.wx;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.MessageRefEntity;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.ArrayList;
import java.util.Date;

@SpringBootTest
class EmosApiApplicationTests {

    @Autowired
    private MessageService messageService;
    @Test
    void contextLoads() {
        for (int i = 1; i <= 100; i++) {
            MessageEntity message = new MessageEntity();
            message.setUuid(IdUtil.simpleUUID());
            message.setSenderId(0);
            message.setSenderName("系统消息");
            message.setMsg("这是第" + i + "条测试消息");
            message.setSendTime(new Date());
            String id=messageService.insertMessage(message);

            MessageRefEntity ref=new MessageRefEntity();
            ref.setMessageId(id);
            ref.setReceiverId(7); //注意：这是接收人ID
            ref.setLastFlag(true);
            ref.setReadFlag(false);
            messageService.insertRef(ref);
        }

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

        String s = "2022-10-17 11:24:25";
        DateTime parse = DateUtil.parse(s);
        boolean b = DateUtil.compare(parse, DateUtil.date()) <= 0;
        System.out.println(b);
    }

    @Test
    void test3() {
        Date date = DateUtil.date();
        System.out.println(date);
        String today = DateUtil.today();
        System.out.println(today);

        DateTime dateTime = DateUtil.date();
        System.out.println(dateTime);

        DateTime parse = DateUtil.parse("2000" + "-" + "01" + "-01");
        System.out.println(parse);


    }

    @Test
    void test4() {

        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        System.out.println(list);

        Integer integer = list.get(0);
        System.out.println(integer);

    }


}



package com.example.emos.wx;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


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
       if (!path.endsWith(".jpg")){
           throw new RuntimeException("报错！");
       }
    }

}

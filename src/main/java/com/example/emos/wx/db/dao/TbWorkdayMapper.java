package com.example.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author 23758
* @description 针对表【tb_workday】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbWorkday
*/
@Mapper
public interface TbWorkdayMapper {

    //查询当天是不是调休日
     Integer searchTodayIsWorkday();


    ArrayList<String> searchWorkdayInRange(HashMap param);
}

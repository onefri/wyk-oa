package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbWorkday;
import org.apache.ibatis.annotations.Mapper;

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
}

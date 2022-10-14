package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbHolidays;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23758
* @description 针对表【tb_holidays(节假日表)】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbHolidays
*/
@Mapper
public interface TbHolidaysMapper {

    //查询当天是否节假日
    Integer searchTodayIsHolidays();

}

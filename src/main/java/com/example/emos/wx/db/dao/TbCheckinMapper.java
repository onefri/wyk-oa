package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 23758
 * @description 针对表【tb_checkin(签到表)】的数据库操作Mapper
 * @createDate 2022-10-10 18:36:24
 * @Entity com.example.emos.wx.db.pojo.TbCheckin
 */
@Mapper
public interface TbCheckinMapper {

    Integer haveCheckin(HashMap param);

    Integer insert(TbCheckin entity);

    HashMap searchTodayCheckin(int userId);

    long searchCheckinDays(int userId);

    ArrayList<HashMap> searchWeekCheckin(HashMap param);

}

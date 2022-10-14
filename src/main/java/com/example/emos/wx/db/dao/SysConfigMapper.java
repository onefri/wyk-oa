package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.SysConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 23758
* @description 针对表【sys_config】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.SysConfig
*/
@Mapper
public interface SysConfigMapper {

     List<SysConfig> selectAllParam();
}

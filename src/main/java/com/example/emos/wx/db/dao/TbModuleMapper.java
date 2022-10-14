package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbModule;

/**
* @author 23758
* @description 针对表【tb_module(模块资源表)】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbModule
*/
public interface TbModuleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbModule record);

    int insertSelective(TbModule record);

    TbModule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbModule record);

    int updateByPrimaryKey(TbModule record);

}

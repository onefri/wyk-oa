package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbAction;

/**
* @author 23758
* @description 针对表【tb_action(行为表)】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbAction
*/
public interface TbActionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbAction record);

    int insertSelective(TbAction record);

    TbAction selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbAction record);

    int updateByPrimaryKey(TbAction record);

}

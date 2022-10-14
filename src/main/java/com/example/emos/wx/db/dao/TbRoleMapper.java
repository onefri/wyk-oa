package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbRole;

/**
* @author 23758
* @description 针对表【tb_role(角色表)】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbRole
*/
public interface TbRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbRole record);

    int insertSelective(TbRole record);

    TbRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbRole record);

    int updateByPrimaryKey(TbRole record);

}

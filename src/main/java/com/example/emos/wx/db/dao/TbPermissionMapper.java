package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbPermission;

/**
* @author 23758
* @description 针对表【tb_permission】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbPermission
*/
public interface TbPermissionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbPermission record);

    int insertSelective(TbPermission record);

    TbPermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbPermission record);

    int updateByPrimaryKey(TbPermission record);

}

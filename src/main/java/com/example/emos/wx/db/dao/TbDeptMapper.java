package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbDept;

/**
* @author 23758
* @description 针对表【tb_dept】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbDept
*/
public interface TbDeptMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbDept record);

    int insertSelective(TbDept record);

    TbDept selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbDept record);

    int updateByPrimaryKey(TbDept record);

}

package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbFaceModel;

/**
* @author 23758
* @description 针对表【tb_face_model】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbFaceModel
*/
public interface TbFaceModelMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbFaceModel record);

    int insertSelective(TbFaceModel record);

    TbFaceModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbFaceModel record);

    int updateByPrimaryKey(TbFaceModel record);

}

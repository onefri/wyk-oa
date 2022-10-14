package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbCity;

/**
* @author 23758
* @description 针对表【tb_city(疫情城市列表)】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbCity
*/
public interface TbCityMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbCity record);

    int insertSelective(TbCity record);

    TbCity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbCity record);

    int updateByPrimaryKey(TbCity record);

}

package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbCity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.jmx.export.annotation.ManagedAttribute;

/**
* @author 23758
* @description 针对表【tb_city(疫情城市列表)】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbCity
*/
@Mapper
public interface TbCityMapper {

    String searchCode(String city);
}

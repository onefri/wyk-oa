package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbMeeting;

/**
* @author 23758
* @description 针对表【tb_meeting(会议表)】的数据库操作Mapper
* @createDate 2022-10-10 18:36:24
* @Entity com.example.emos.wx.db.pojo.TbMeeting
*/
public interface TbMeetingMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbMeeting record);

    int insertSelective(TbMeeting record);

    TbMeeting selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbMeeting record);

    int updateByPrimaryKey(TbMeeting record);

}

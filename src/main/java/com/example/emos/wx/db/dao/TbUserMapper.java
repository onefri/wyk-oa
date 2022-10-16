package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.Set;

/**
 * @author 23758
 * @description 针对表【tb_user(用户表)】的数据库操作Mapper
 * @createDate 2022-10-10 18:36:24
 * @Entity com.example.emos.wx.db.pojo.TbUser
 */

@Mapper
public interface TbUserMapper {


    boolean haveRootUser();

    int insert(HashMap param);

    int searchIdByOpenId(String openId);

    //查询用户权限
    Set<String> searchUserPermissions(int userId);

      TbUser searchById(int userId);

    HashMap searchNameAndDept(int userId);

    String searchUserHiredate(int userId);

     HashMap searchUserSummary(int userId);

}

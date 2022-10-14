package com.example.emos.wx.service;


import java.util.HashMap;

public interface CheckinService {

    String haveCheckin(Integer userId,String date );

    public void checkin(HashMap param);
}

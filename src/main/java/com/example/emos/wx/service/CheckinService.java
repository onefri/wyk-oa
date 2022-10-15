package com.example.emos.wx.service;


import com.example.emos.wx.controller.form.CheckinForm;

import java.util.HashMap;

public interface CheckinService {

    String haveCheckin(Integer userId,String date );

    void checkin(CheckinForm form, int userId, String path);


    void createFaceModel(int userId, String path);
}

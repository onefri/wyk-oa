package com.example.emos.wx.service;


import com.example.emos.wx.controller.form.CheckinForm;

import java.util.ArrayList;
import java.util.HashMap;

public interface CheckinService {

    String haveCheckin(Integer userId, String date);

    void checkin(HashMap param);


    void createFaceModel(int userId, String path);


    HashMap searchTodayCheckin(int userId);

    long searchCheckinDays(int userId);

    ArrayList<HashMap> searchWeekCheckin(HashMap param);

    ArrayList<HashMap> searchMonthCheckin(HashMap param);

}

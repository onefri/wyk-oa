package com.example.emos.wx.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.example.emos.wx.db.constants.SystemConstants;
import com.example.emos.wx.db.dao.TbCheckinMapper;
import com.example.emos.wx.db.dao.TbFaceModelMapper;
import com.example.emos.wx.db.dao.TbHolidaysMapper;
import com.example.emos.wx.db.dao.TbWorkdayMapper;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
@Scope("prototype")
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private TbHolidaysMapper holidaysMapper;

    @Autowired
    private TbWorkdayMapper workdayMapper;

    @Autowired
    private TbCheckinMapper checkinMapper;

    @Autowired
    private SystemConstants constants;

    @Autowired
    private TbFaceModelMapper faceModelMapper;

    @Value("${emos.face.checkinUrl}")
    private String checkinUrl;

    @Override
    public String haveCheckin(Integer userId, String date) {
        boolean bool_1 = holidaysMapper.searchTodayIsHolidays() != null ? true : false;
        boolean bool_2 = workdayMapper.searchTodayIsWorkday() != null ? true : false;

        String type = "工作日";

        if (DateUtil.date().isWeekend()) {
            type = "节假日";
        }

        if (bool_1) {
            type = "节假日";
        } else if (bool_2) {
            type = "工作日";
        }

        if (type.equals("节假日")) {
            return "节假日不需要考勤";
        } else {
            //创建当前日期
            DateTime now = DateUtil.date();
            //上班考勤开始时间
            String start = DateUtil.today() + " " + constants.attendanceStartTime;
            //上班考勤截止时间
            String end = DateUtil.today() + " " + constants.attendanceEndTime;

            DateTime attendanceStart = DateUtil.parse(start);
            DateTime attendanceEnd = DateUtil.parse(end);

            if (now.before(attendanceStart)) {
                return "没有到考勤的时间！";
            } else if (now.after(attendanceEnd)) {
                return "考勤时间已经结束！";
            } else {
                //都不在这返回查询是否考勤过
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("date",date);
                map.put("start", start);
                map.put("end", end);

                boolean bool = checkinMapper.haveCheckin(map) != null ? true : false;

                return bool ? "今日已考勤，不需要考勤" : "可以考勤";

            }
        }
    }

    @Override
    public void checkin(HashMap param) {
        //判断签到
        Date d1 = DateUtil.date();  //当前时间
        Date d2 = DateUtil.parse(DateUtil.today() + " " + constants.attendanceTime);  //上班时间
        Date d3 = DateUtil.parse(DateUtil.today() + " " + constants.attendanceEndTime); //签到结束时间
        int status = 1;
        if (d1.compareTo(d2) <= 0) {
            status = 1; // 正常签到
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) < 0) {
            status = 2; //迟到
        }
        //查询签到人的人脸模型数据
        int userId= (Integer) param.get("userId");
        String faceModel=faceModelMapper.searchFaceModel(userId);
        if (faceModel == null) {
            throw new EmosException("不存在人脸模型");
        } else {
            String path=(String)param.get("path");
            HttpRequest request = HttpUtil.createPost(checkinUrl);
            request.form("photo", FileUtil.file(path), "targetModel", faceModel);
            HttpResponse response = request.execute();
            if (response.getStatus() != 200) {
                log.error("人脸识别服务异常");
                throw new EmosException("人脸识别服务异常");
            }
            String body = response.body();
            if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
                throw new EmosException(body);
            } else if ("False".equals(body)) {
                throw new EmosException("签到无效，非本人签到");
            } else if ("True".equals(body)) {
                //TODO 这里要获取签到地区新冠疫情风险等级
                //TODO 保存签到记录
            }
        }

    }
}

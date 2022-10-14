package com.example.emos.wx.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.emos.wx.db.constants.SystemConstants;
import com.example.emos.wx.db.dao.TbCheckinMapper;
import com.example.emos.wx.db.dao.TbHolidaysMapper;
import com.example.emos.wx.db.dao.TbWorkdayMapper;
import com.example.emos.wx.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Scope("prototype")
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private TbHolidaysMapper holidaysMapper;

    @Autowired
    private TbWorkdayMapper workdayMapper;

    @Autowired
    private TbCheckinMapper checkinMapper;

    @Autowired
    private SystemConstants systemConstants;

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
            String start = DateUtil.today() + " " + systemConstants.attendanceStartTime;
            //上班考勤截止时间
            String end = DateUtil.today() + " " + systemConstants.attendanceEndTime;

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
}

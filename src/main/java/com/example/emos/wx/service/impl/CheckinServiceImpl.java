package com.example.emos.wx.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.example.emos.wx.controller.form.CheckinForm;
import com.example.emos.wx.db.EmailTask;
import com.example.emos.wx.db.constants.SystemConstants;
import com.example.emos.wx.db.dao.*;
import com.example.emos.wx.db.pojo.TbCheckin;
import com.example.emos.wx.db.pojo.TbFaceModel;
import com.example.emos.wx.db.pojo.TbUser;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @Value("${emos.face.createFaceModelUrl}")
    private String createFaceModelUrl;

    @Value("${emos.email.hr}")
    private String hrEmail;

    @Autowired
    private EmailTask emailTask;

    @Autowired
    private TbCityMapper cityMapper;

    @Autowired
    private TbUserMapper userMapper;

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
                map.put("date", date);
                map.put("start", start);
                map.put("end", end);

                boolean bool = checkinMapper.haveCheckin(map) != null ? true : false;

                return bool ? "今日已考勤，不需要考勤" : "可以考勤";

            }
        }
    }

    @Override
    public void checkin(CheckinForm form, int userId, String path) {
        //获取当前时间
        Date d1 = DateUtil.date();
        //获取上班的时间
        Date d2 = DateUtil.parse(DateUtil.today() + " " + constants.attendanceTime);
        //获取考勤结束时间
        Date d3 = DateUtil.parse(DateUtil.today() + " " + constants.attendanceTime);

        int status = 1;
        if (d1.compareTo(d2) <= 0) {
            status = 1;
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) < 0) {
            status = 2;
        }

        //查询人脸模型
        String faceModel = faceModelMapper.searchFaceModel(userId);
        if (faceModel == null) {
            throw new EmosException("获取人脸失败！");
        } else {
            HttpRequest request = HttpUtil.createPost(checkinUrl);

            request.form("photo", path, "targetModel", faceModel);

            HttpResponse response = request.execute();
            if (response.getStatus() != 200) {
                throw new EmosException("获取人脸异常！");
            }
            //接受响应体
            String body = response.body();
            if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
                throw new EmosException(body);
            } else if ("False".equals(body)) {
                throw new EmosException("签到无效，非本人签到");
            } else if ("True".equals(body)) {
                // 这里要获取签到地区新冠疫情风险等级
                int risk = 1;
                if (form.getCity() != null && form.getCity().length() > 0 &&
                        form.getDistrict() != null && form.getDistrict().length() > 0) {
                    String code = cityMapper.searchCode(form.getCity());
                    try {
                        String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + form.getDistrict();
                        Document document = Jsoup.connect(url).get();
                        Elements elements = document.getElementsByClass("list");
                        for (Element one : elements) {
                            String result = one.text().split(" ")[1];
                            if ("高风险".equals(result)) {
                                HashMap<String, String> map = userMapper.searchNameAndDept(userId);
                                String name = map.get("name");
                                String deptName = map.get("dept_name");
                                deptName = deptName != null ? deptName : "";
                                SimpleMailMessage message = new SimpleMailMessage();
                                message.setTo(hrEmail);
                                message.setSubject("员工" + name + "身处高风险疫情地区警告");
                                message.setText(deptName + "员工" + name + "，" + DateUtil.format(new Date(), "yyyy年MM月dd日") + "处于" +
                                        form.getAddress() + "，属于新冠疫情高风险地区，请及时与该员工联系，核实情况！");

                            } else if ("中风险".equals(result)) {
                                risk = risk < 2 ? 2 : risk;
                            }
                        }
                    } catch (IOException e) {
                        log.error("执行异常", e);
                        throw new EmosException("获取风险等级失败");
                    }


                    // 保存签到记录
                    TbCheckin entity = new TbCheckin();
                    entity.setUserId(userId);
                    entity.setAddress(form.getAddress());
                    entity.setCountry(form.getCountry());
                    entity.setProvince(form.getProvince());
                    entity.setCity(form.getCity());
                    entity.setDistrict(form.getDistrict());
                    entity.setStatus(status);
                    entity.setRisk(risk);
                    entity.setDate(DateUtil.today());
                    entity.setCreateTime(d1);
                    checkinMapper.insert(entity);


                }

            }


        }
    }

    @Override
    public void createFaceModel(int userId, String path) {
        HttpRequest request = HttpUtil.createPost(createFaceModelUrl);
        request.form("photo", FileUtil.file(path));
        HttpResponse response = request.execute();
        String body = response.body();
        if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
            throw new EmosException(body);
        } else {
            TbFaceModel entity = new TbFaceModel();
            entity.setUserId(userId);
            entity.setFaceModel(body);
            faceModelMapper.insert(entity);
        }

    }
}




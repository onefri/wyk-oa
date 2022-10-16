package com.example.emos.wx.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.example.emos.wx.db.EmailTask;
import com.example.emos.wx.db.constants.SystemConstants;
import com.example.emos.wx.db.dao.*;

import com.example.emos.wx.db.pojo.TbCheckin;
import com.example.emos.wx.db.pojo.TbFaceModel;
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

import java.util.ArrayList;
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

    @Value("${emos.code}")
    private String code;

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
            String end = DateUtil.today() + " " + "22:00";

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
    public void checkin(HashMap param) {
        Date d1=DateUtil.date();
        Date d2=DateUtil.parse(DateUtil.today()+" "+constants.attendanceTime);
        Date d3=DateUtil.parse(DateUtil.today()+" "+"20:00");
        int status=1;
        if(d1.compareTo(d2)<=0){
            status=1;
        }
        else if(d1.compareTo(d2)>0&&d1.compareTo(d3)<0){
            status=2;
        }
        else{
            throw new EmosException("超出考勤时间段，无法考勤");
        }
        int userId= (Integer) param.get("userId");
        String faceModel=faceModelMapper.searchFaceModel(userId);
        if(faceModel==null){
            throw new EmosException("不存在人脸模型");
        }
        else{
            String path=(String)param.get("path");
            HttpRequest request= HttpUtil.createPost(checkinUrl);
            request.form("photo", FileUtil.file(path),"targetModel",faceModel);
            request.form("code",code);
            HttpResponse response=request.execute();
            if(response.getStatus()!=200){
                log.error("人脸识别服务异常");
                throw new EmosException("人脸识别服务异常");
            }
            String body=response.body();
            if("无法识别出人脸".equals(body)||"照片中存在多张人脸".equals(body)){
                throw new EmosException(body);
            }
            else if("False".equals(body)){
                throw new EmosException("签到无效，非本人签到");
            }
            else if("True".equals(body)){
                //查询疫情风险等级
                int risk=1;
                String city= (String) param.get("city");
                String district= (String) param.get("district");
                String address= (String) param.get("address");
                String country= (String) param.get("country");
                String province= (String) param.get("province");
                if(!StrUtil.isBlank(city)&&!StrUtil.isBlank(district)){
                    String code=cityMapper.searchCode(city);
                    try{
                        String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
                        Document document=Jsoup.connect(url).get();
                        Elements elements=document.getElementsByClass("list");
                        if(elements.size()>0){
                            Element element=elements.get(0);
                            String result=element.select("p:last-child").text();

                           // result="高风险";
                            if("高风险".equals(result)){
                                risk=3;
                                //发送告警邮件
                                HashMap<String,String> map=userMapper.searchNameAndDept(userId);
                                String name = map.get("name");
                                String deptName = map.get("dept_name");
                                deptName = deptName != null ? deptName : "";
                                SimpleMailMessage message=new SimpleMailMessage();
                                message.setTo(hrEmail);
                                message.setSubject("员工" + name + "身处高风险疫情地区警告");
                                message.setText(deptName + "员工" + name + "，" + DateUtil.format(new Date(), "yyyy年MM月dd日") + "处于" + address + "，属于新冠疫情高风险地区，请及时与该员工联系，核实情况！");
                                emailTask.sendAsync(message);
                            }
                            else if("中风险".equals(result)){
                                risk=2;
                            }
                        }
                    }catch (Exception e){
                        log.error("执行异常",e);
                        throw new EmosException("获取风险等级失败");
                    }
                }
                //保存签到记录
                TbCheckin entity = new TbCheckin();
                entity.setUserId(userId);
                entity.setAddress(address);
                entity.setCountry(country);
                entity.setProvince(province);
                entity.setCity(city);
                entity.setDistrict(district);
                entity.setStatus((byte) status);
                entity.setRisk(risk);
                entity.setDate(DateUtil.today());
                entity.setCreateTime(d1);
                checkinMapper.insert(entity);
            }
        }
    }
    @Override
    public void createFaceModel(int userId, String path) {
        HttpRequest request=HttpUtil.createPost(createFaceModelUrl);
        request.form("photo",FileUtil.file(path));
        request.form("code",code);
        HttpResponse response=request.execute();
        String body=response.body();
        if("无法识别出人脸".equals(body)||"照片中存在多张人脸".equals(body)){
            throw new EmosException(body);
        }
        else{
            TbFaceModel entity=new TbFaceModel();
            entity.setUserId(userId);
            entity.setFaceModel(body);
            faceModelMapper.insert(entity);
        }
    }

    @Override
    public HashMap searchTodayCheckin(int userId) {
        return checkinMapper.searchTodayCheckin(userId);
    }

    @Override
    public long searchCheckinDays(int userId) {
        return checkinMapper.searchCheckinDays(userId);
    }

    @Override
    public ArrayList<HashMap> searchWeekCheckin(HashMap param) {
        //查询出考勤的范围统计（一般周一到周五）
        ArrayList<HashMap> checkinList = checkinMapper.searchWeekCheckin(param);
        //查询出特殊的节假日
        ArrayList<String> holidaysList = holidaysMapper.searchHolidaysInRange(param);
        //查询出特殊的工作日
        ArrayList<String> workdayList = workdayMapper.searchWorkdayInRange(param);

        //开始时间
        DateTime startDate = DateUtil.parseDate(param.get("startDate").toString());
        //结束时间
        DateTime endDate = DateUtil.parseDate(param.get("endDate").toString());
        //连续七天日期对象
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);

        ArrayList<HashMap> list = new ArrayList<>();
        for (DateTime dateTime : range) {
            String date = dateTime.toString("yyyy-MM-dd");
            String type = "工作日";
            if (dateTime.isWeekend()) {
                type = "节假日";
            }

            //如果节假日不等于null,并且查询的节假日也包含查询的日期范围
            if (holidaysList != null && holidaysList.contains(date)) {
                type = "节假日";
            } else if (workdayList != null && workdayList.contains(date)) {
                type = "工作日";
            }

            String status = "";
            //如果是工作日，并且已经过了当天
            if (type.equals("工作日") && DateUtil.compare(dateTime, DateUtil.date()) <= 0) {
                status = "缺勤";
                boolean flag = false;
                for (HashMap<String, String> map : checkinList) {
                    if (map.containsValue(date)) {
                        //如果你考勤了，然后还没有结束
                        status = map.get("status");
                        //查询到考勤结果为true
                        flag = true;
                        break;
                    }
                }
                //如果当天还没有结束就判断缺勤不合理
                DateTime endTime = DateUtil.parse(DateUtil.today() + " " + constants.attendanceEndTime);
                String today = DateUtil.today();
                //考勤还没有结束，并且当天还没有结束
                if (date.equals(today) && DateUtil.date().isBefore(endTime) && flag == false) {
                    status = "";
                }
            }
            HashMap map = new HashMap();
            map.put("date", date);
            map.put("status", status);
            map.put("type", type);
            map.put("day", dateTime.dayOfWeekEnum().toChinese("周"));
            list.add(map);
        }
        return list;
    }

    @Override
    public ArrayList<HashMap> searchMonthCheckin(HashMap param) {
        return this.searchWeekCheckin(param);
    }
}


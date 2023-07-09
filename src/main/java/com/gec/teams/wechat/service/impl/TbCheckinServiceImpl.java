package com.gec.teams.wechat.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.teams.wechat.common.utils.FaceComponent;
import com.gec.teams.wechat.config.SystemConstants;
import com.gec.teams.wechat.entity.TbCheckin;
import com.gec.teams.wechat.entity.TbUser;
import com.gec.teams.wechat.exception.TeamsException;
import com.gec.teams.wechat.mapper.TbCheckinMapper;
import com.gec.teams.wechat.mapper.TbHolidaysMapper;
import com.gec.teams.wechat.mapper.TbUserMapper;
import com.gec.teams.wechat.mapper.TbWorkdayMapper;
import com.gec.teams.wechat.service.TbCheckinService;
import com.gec.teams.wechat.task.EmailTask;
import com.tencentcloudapi.iai.v20180301.models.VerifyPersonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Administrator
 * @description 针对表【tb_checkin(签到表)】的数据库操作Service实现
 * @createDate 2023-07-03 17:05:26
 */
@Service
@Scope("prototype")
@Slf4j
public class TbCheckinServiceImpl extends ServiceImpl<TbCheckinMapper, TbCheckin>
        implements TbCheckinService {
    @Autowired
    private SystemConstants systemConstants;
    @Autowired
    private TbHolidaysMapper holidaysMapper;
    @Autowired
    private TbWorkdayMapper workdayMapper;
    @Autowired
    private TbCheckinMapper checkinMapper;
    @Autowired
    private FaceComponent faceComponent;
    @Autowired
    private TbUserMapper userMapper;
    @Value("${teams.email.hr}")
    private String hr;
    @Autowired
    EmailTask emailTask;

    @Override
    public String validCanCheckin(int userId, String date) {
        //1.查询出当天 要么是 特殊的节假日要么是特殊的工作日 , 要么都不是 就按照正常周一到周五进行判断
        boolean bool_1 = holidaysMapper.searchTodayIsHolidays() != null ? true : false;
        boolean bool_2 = workdayMapper.searchTodayIsWorkday() != null ? true : false;
        String type = "工作日";
        if (DateUtil.date().isWeekend()) {//2.判断当前日期是不是周末(周六或周日)
            type = "节假日";
        }//3.根据上面查询结果修正当前是 特殊的节假日 还是特殊的工作日
        if (bool_1) {
            type = "节假日";
        } else if (bool_2) {
            type = "工作日";
        }

        if (type.equals("节假日")) {
            return "节假日不需要考勤";
        } else {//当前是工作日,查询当前时间是否能够考勤
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + systemConstants.attendanceStartTime;
            String end = DateUtil.today() + " " + systemConstants.attendanceEndTime;
            log.info(start);
            log.info(end);
            DateTime attendanceStart = DateUtil.parse(start);
            DateTime attendanceEnd = DateUtil.parse(end);
            if (now.isBefore(attendanceStart)) {
                return "没到上班考勤开始时间";
            } else if (now.isAfter(attendanceEnd)) {
                return "超过了上班考勤结束时间";
            } else {//在考勤时间范围内,需要查询是否有过考勤记录
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("date", date);
                map.put("start", start);
                map.put("end", end);
                boolean bool = checkinMapper.haveCheckin(map) != null ? true : false;
                return bool ? "今日已经考勤，不用重复考勤" : "可以考勤";
            }
        }
    }

    @Override
    public void checkin(HashMap hashMap) {
        Date d1 = DateUtil.date();
        Date d2 = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceTime);
        Date d3 = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime);
        int status = 1;
        if (d1.compareTo(d2) <= 0) {
            status = 1;
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) < 0) {
            status = 2;
        } else {
            throw new TeamsException("超出考勤时间段，无法考勤");
        }
        int userId = (Integer) hashMap.get("userId");
        Boolean userInfo = faceComponent.getUserInfo(userId + "");
//        Boolean userInfo = true;
        if (userInfo == null) {
            throw new TeamsException("不存在人脸模型");
        } else {
            VerifyPersonResponse resp = faceComponent.verifyUser(userId + "", (MultipartFile) hashMap.get("file"));
            Boolean isMatch = resp.getIsMatch();
//            Boolean isMatch =true;
            if (!isMatch) {
                throw new TeamsException("签到无效，非本人签到");
            } else {
                String city = (String) hashMap.get("city");
                String district = (String) hashMap.get("district");
                String address = (String) hashMap.get("address");
                String country = (String) hashMap.get("country");
                String province = (String) hashMap.get("province");


                //发送邮件的代码
                SimpleMailMessage message = new SimpleMailMessage();
                //指定接受邮件的 邮箱
                message.setTo(hr);
                //设置要发送的邮件的 标题
                message.setSubject("员工签到成功！");
                //设置要发送的邮件的内容
                message.setText("领导，员工已经签到成功,在"+district+"街道上班！！！！");
                //调用一下发送邮件的代码
                emailTask.sendAsync(message);

                //保存签到记录
                TbCheckin entity = new TbCheckin();
                entity.setUserId(userId);
                entity.setAddress(address);
                entity.setCountry(country);
                entity.setProvince(province);
                entity.setCity(city);
                entity.setDistrict(district);
                entity.setStatus(status);
                entity.setRisk(1);
                entity.setDate(DateUtil.today());
                entity.setCreateTime(d1);
                checkinMapper.insert(entity);
            }
        }
    }

    @Override
    public void createFaceModel(int userId, MultipartFile file) {
        TbUser tbUser = userMapper.selectById(userId);

        Boolean user = faceComponent.createUser(tbUser.getNickname(), userId + "", file);
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
        ArrayList<HashMap> checkinList = checkinMapper.searchWeekCheckin(param);
        ArrayList holidaysList = holidaysMapper.searchHolidaysInRange(param);
        ArrayList workdayList = workdayMapper.searchWorkdayInRange(param);
        DateTime startDate = DateUtil.parseDate(param.get("startDate").toString());
        DateTime endDate = DateUtil.parseDate(param.get("endDate").toString());
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);
        ArrayList<HashMap> list = new ArrayList<>();
        range.forEach(one -> {
            String date = one.toString("yyyy-MM-dd");
            String type = "工作日";
            if (one.isWeekend()) {
                type = "节假日";
            }
            if (holidaysList != null && holidaysList.contains(date)) {
                type = "节假日";
            } else if (workdayList != null && workdayList.contains(date)) {
                type = "工作日";
            }
            String status = "";
            //DateUtil.compare(考勤日期one,当前日期<=0) 说明已经当前考勤日期在本周 已经发生过
            if (type.equals("工作日") && DateUtil.compare(one, DateUtil.date()) <= 0) {
                status = "缺勤";
                boolean flag = false;//查找这天的考勤结果 并且考勤没有结束
                for (HashMap<String, String> map : checkinList) {
                    if (map.containsValue(date)) {
                        status = map.get("status");
                        flag = true;
                        break;
                    }
                }
                //如果是当天还没考勤结束的情况
                DateTime endTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime);
                String today = DateUtil.today();//判断当前时间是否早于考勤结束时间
                if (date.equals(today) && DateUtil.date().isBefore(endTime) && flag == false) {
                    status = "";
                }
            }
            HashMap map = new HashMap();
            map.put("date", date);
            map.put("status", status);
            map.put("type", type);
            map.put("day", one.dayOfWeekEnum().toChinese("周"));
            list.add(map);
        });
        return list;
    }

}






package com.gec.teams.wechat.service.impl;

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
import com.tencentcloudapi.iai.v20180301.models.VerifyPersonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        implements TbCheckinService{
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
            if(now.isBefore(attendanceStart)){
                return "没到上班考勤开始时间";
            }
            else if(now.isAfter(attendanceEnd)){
                return "超过了上班考勤结束时间";
            }else {//在考勤时间范围内,需要查询是否有过考勤记录
                HashMap map=new HashMap();
                map.put("userId",userId);
                map.put("date",date);
                map.put("start",start);
                map.put("end",end);
                boolean bool=checkinMapper.haveCheckin(map)!=null?true:false;
                return bool?"今日已经考勤，不用重复考勤" : "可以考勤";
            }
        }
    }

    @Override
    public void checkin(HashMap hashMap) {
        Date d1=DateUtil.date();
        Date d2=DateUtil.parse(DateUtil.today()+" "+systemConstants.attendanceTime);
        Date d3=DateUtil.parse(DateUtil.today()+" "+systemConstants.attendanceEndTime);
        int status=1;
        if(d1.compareTo(d2)<=0){
            status=1;
        }
        else if(d1.compareTo(d2)>0&&d1.compareTo(d3)<0){
            status=2;
        }
        else{
            throw new TeamsException("超出考勤时间段，无法考勤");
        }
        int userId= (Integer) hashMap.get("userId");
        Boolean userInfo = faceComponent.getUserInfo(userId + "");
        if(userInfo==null){
            throw new TeamsException("不存在人脸模型");
        }else{
            VerifyPersonResponse resp = faceComponent.verifyUser(userId + "", (MultipartFile) hashMap.get("file"));

            Boolean isMatch = resp.getIsMatch();
            if(!isMatch){
                throw new TeamsException("签到无效，非本人签到");
            }else {
                String city= (String) hashMap.get("city");
                String district= (String) hashMap.get("district");
                String address= (String) hashMap.get("address");
                String country= (String) hashMap.get("country");
                String province= (String) hashMap.get("province");
                //保存签到记录
                TbCheckin entity=new TbCheckin();
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

}






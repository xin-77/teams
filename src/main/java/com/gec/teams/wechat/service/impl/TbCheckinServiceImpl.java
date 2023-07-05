package com.gec.teams.wechat.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.teams.wechat.config.SystemConstants;
import com.gec.teams.wechat.entity.TbCheckin;
import com.gec.teams.wechat.mapper.TbCheckinMapper;
import com.gec.teams.wechat.mapper.TbHolidaysMapper;
import com.gec.teams.wechat.mapper.TbWorkdayMapper;
import com.gec.teams.wechat.service.TbCheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author Administrator
 * @description 针对表【tb_checkin(签到表)】的数据库操作Service实现
 * @createDate 2023-07-03 17:04:31
 */
@Service
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
}





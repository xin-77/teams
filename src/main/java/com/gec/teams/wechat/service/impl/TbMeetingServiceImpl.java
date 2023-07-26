package com.gec.teams.wechat.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.teams.wechat.entity.TbMeeting;
import com.gec.teams.wechat.exception.TeamsException;
import com.gec.teams.wechat.mapper.TbMeetingMapper;
import com.gec.teams.wechat.service.TbMeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 * @description 针对表【tb_meeting(会议表)】的数据库操作Service实现
 * @createDate 2023-07-03 17:04:31
 */
@Service
@Slf4j
public class TbMeetingServiceImpl extends ServiceImpl<TbMeetingMapper, TbMeeting> implements TbMeetingService {
    @Autowired
    private TbMeetingMapper meetingMapper;

    @Override
    public void insertMeeting(TbMeeting meeting) {
        int row = meetingMapper.insertMeeting(meeting);
        if (row != 1) {
            throw new TeamsException("会议添加失败！");
        }

    }

    @Override
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap map) {
        ArrayList<HashMap> list = meetingMapper.searchMyMeetingListByPage(map);
        String date = null;//将会议通过时间进行分组
        log.info(String.valueOf(list.size()));
        ArrayList resultList = new ArrayList();
        HashMap resultMap = null;
        JSONArray array = null;
        for (HashMap meeting : list) {
            String temp = meeting.get("date").toString();
            if (!temp.equals(date)) {
                date = temp;
                resultMap = new HashMap();
                resultMap.put("date", date);
                array = new JSONArray();
                resultMap.put("list", array);
                resultList.add(resultMap);
            }
            array.put(meeting);
        }
        return resultList;
    }

    @Override
    public HashMap searchMeetingById(int id) {
        HashMap map = meetingMapper.searchMeetingById(id);
        ArrayList<HashMap> list = meetingMapper.searchMeetingMembers(id);
        map.put("members", list);
        return map;
    }

    @Override
    public void updateMeetingInfo(HashMap param) {
        int row = meetingMapper.updateMeetingInfo(param);
        if (row != 1) {
            throw new TeamsException("会议更新失败");
        }

    }

    @Override
    public void deleteMeetingById(int id) {

        HashMap meeting = meetingMapper.searchMeetingById(id);
        DateTime date = DateUtil.parse(meeting.get("date") + " " + meeting.get("start"));
        DateTime now = DateUtil.date();
        if(now.isAfterOrEquals(date.offset(DateField.MINUTE,-20))){
            throw new TeamsException("距离会议开始不足20分钟,不能删除会议");
        }
        int i = meetingMapper.deleteMeetingById(id);
        if(i != 1){
            throw new TeamsException("会议删除失败！");
        }
    }
}





package com.gec.teams.wechat.service;

import com.gec.teams.wechat.entity.TbMeeting;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 * @description 针对表【tb_meeting(会议表)】的数据库操作Service
 * @createDate 2023-07-03 17:04:31
 */
public interface TbMeetingService extends IService<TbMeeting> {
    void insertMeeting(TbMeeting meeting);

    ArrayList<HashMap> searchMyMeetingListByPage(HashMap map);

    HashMap searchMeetingById(int id);

    void updateMeetingInfo(HashMap param);

    void deleteMeetingById(int id);

}

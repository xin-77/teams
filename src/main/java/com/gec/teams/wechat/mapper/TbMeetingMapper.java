package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbMeeting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author Administrator
* @description 针对表【tb_meeting(会议表)】的数据库操作Mapper
* @createDate 2023-07-03 17:04:31
* @Entity com.gec.teams.wechat.entity.TbMeeting
*/
@Mapper
public interface TbMeetingMapper extends BaseMapper<TbMeeting> {
     int insertMeeting(TbMeeting meeting);

     ArrayList<HashMap> searchMyMeetingListByPage(HashMap map);

     HashMap searchMeetingById(int id);

     ArrayList<HashMap> searchMeetingMembers(int id);

     int updateMeetingInfo(HashMap param);

     int deleteMeetingById(int id);

}





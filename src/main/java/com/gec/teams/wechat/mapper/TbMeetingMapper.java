package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbMeetingentity;

/**
* @author Administrator
* @description 针对表【tb_meeting(会议表)】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbMeetingentity
*/
public interface TbMeetingMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbMeetingentity record);

    int insertSelective(TbMeetingentity record);

    TbMeetingentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbMeetingentity record);

    int updateByPrimaryKey(TbMeetingentity record);

}

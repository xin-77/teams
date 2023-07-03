package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbCheckinentity;

/**
* @author Administrator
* @description 针对表【tb_checkin(签到表)】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbCheckinentity
*/
public interface TbCheckinMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbCheckinentity record);

    int insertSelective(TbCheckinentity record);

    TbCheckinentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbCheckinentity record);

    int updateByPrimaryKey(TbCheckinentity record);

}

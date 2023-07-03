package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.domain.SysConfigentity;

/**
* @author Administrator
* @description 针对表【sys_config】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.SysConfigentity
*/
public interface SysConfigMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysConfigentity record);

    int insertSelective(SysConfigentity record);

    SysConfigentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysConfigentity record);

    int updateByPrimaryKey(SysConfigentity record);

}

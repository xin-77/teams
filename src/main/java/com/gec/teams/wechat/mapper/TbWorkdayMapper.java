package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbWorkdayentity;

/**
* @author Administrator
* @description 针对表【tb_workday】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbWorkdayentity
*/
public interface TbWorkdayMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbWorkdayentity record);

    int insertSelective(TbWorkdayentity record);

    TbWorkdayentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbWorkdayentity record);

    int updateByPrimaryKey(TbWorkdayentity record);

}

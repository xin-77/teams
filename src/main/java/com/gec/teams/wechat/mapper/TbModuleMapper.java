package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbModuleentity;

/**
* @author Administrator
* @description 针对表【tb_module(模块资源表)】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbModuleentity
*/
public interface TbModuleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbModuleentity record);

    int insertSelective(TbModuleentity record);

    TbModuleentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbModuleentity record);

    int updateByPrimaryKey(TbModuleentity record);

}

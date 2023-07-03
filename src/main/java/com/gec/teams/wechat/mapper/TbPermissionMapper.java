package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.domain.TbPermissionentity;

/**
* @author Administrator
* @description 针对表【tb_permission】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbPermissionentity
*/
public interface TbPermissionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbPermissionentity record);

    int insertSelective(TbPermissionentity record);

    TbPermissionentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbPermissionentity record);

    int updateByPrimaryKey(TbPermissionentity record);

}

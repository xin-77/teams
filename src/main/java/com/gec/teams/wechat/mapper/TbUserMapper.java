package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbUserentity;

/**
* @author Administrator
* @description 针对表【tb_user(用户表)】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbUserentity
*/
public interface TbUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbUserentity record);

    int insertSelective(TbUserentity record);

    TbUserentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbUserentity record);

    int updateByPrimaryKey(TbUserentity record);

}

package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbRoleentity;

/**
* @author Administrator
* @description 针对表【tb_role(角色表)】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbRoleentity
*/
public interface TbRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbRoleentity record);

    int insertSelective(TbRoleentity record);

    TbRoleentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbRoleentity record);

    int updateByPrimaryKey(TbRoleentity record);

}

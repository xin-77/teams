package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.domain.TbActionentity;

/**
* @author Administrator
* @description 针对表【tb_action(行为表)】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbActionentity
*/
public interface TbActionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbActionentity record);

    int insertSelective(TbActionentity record);

    TbActionentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbActionentity record);

    int updateByPrimaryKey(TbActionentity record);

}

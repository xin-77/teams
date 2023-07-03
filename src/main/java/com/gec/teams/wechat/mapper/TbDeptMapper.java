package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbDeptentity;

/**
* @author Administrator
* @description 针对表【tb_dept】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbDeptentity
*/
public interface TbDeptMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbDeptentity record);

    int insertSelective(TbDeptentity record);

    TbDeptentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbDeptentity record);

    int updateByPrimaryKey(TbDeptentity record);

}

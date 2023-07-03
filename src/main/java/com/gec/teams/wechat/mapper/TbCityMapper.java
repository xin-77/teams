package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbCityentity;

/**
* @author Administrator
* @description 针对表【tb_city(疫情城市列表)】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbCityentity
*/
public interface TbCityMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbCityentity record);

    int insertSelective(TbCityentity record);

    TbCityentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbCityentity record);

    int updateByPrimaryKey(TbCityentity record);

}

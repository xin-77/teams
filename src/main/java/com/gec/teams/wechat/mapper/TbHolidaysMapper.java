package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbHolidaysentity;

/**
* @author Administrator
* @description 针对表【tb_holidays(节假日表)】的数据库操作Mapper
* @createDate 2023-07-03 15:42:16
* @Entity com.gec.teams.wechat.domain.TbHolidaysentity
*/
public interface TbHolidaysMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TbHolidaysentity record);

    int insertSelective(TbHolidaysentity record);

    TbHolidaysentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbHolidaysentity record);

    int updateByPrimaryKey(TbHolidaysentity record);

}

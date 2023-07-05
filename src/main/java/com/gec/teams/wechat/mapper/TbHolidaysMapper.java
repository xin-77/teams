package com.gec.teams.wechat.mapper;

import com.gec.teams.wechat.entity.TbHolidays;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Administrator
* @description 针对表【tb_holidays(节假日表)】的数据库操作Mapper
* @createDate 2023-07-03 17:04:31
* @Entity com.gec.teams.wechat.entity.TbHolidays
*/
public interface TbHolidaysMapper extends BaseMapper<TbHolidays> {
    public Integer searchTodayIsHolidays();
}





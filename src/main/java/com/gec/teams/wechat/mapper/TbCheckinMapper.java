package com.gec.teams.wechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.teams.wechat.entity.TbCheckin;

import java.util.HashMap;

/**
* @author Administrator
* @description 针对表【tb_checkin(签到表)】的数据库操作Mapper
* @createDate 2023-07-03 17:04:31
* @Entity com.gec.teams.wechat.entity.TbCheckin
*/
public interface TbCheckinMapper extends BaseMapper<TbCheckin> {
    public Integer haveCheckin(HashMap param);
}





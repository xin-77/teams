package com.gec.teams.wechat.service;

import com.gec.teams.wechat.entity.TbCheckin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【tb_checkin(签到表)】的数据库操作Service
* @createDate 2023-07-03 17:04:31
*/
public interface TbCheckinService extends IService<TbCheckin> {
    public String validCanCheckin(int userId,String date);
}

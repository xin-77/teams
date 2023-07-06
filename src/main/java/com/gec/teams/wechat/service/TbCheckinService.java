package com.gec.teams.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.teams.wechat.entity.TbCheckin;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author Administrator
* @description 针对表【tb_checkin(签到表)】的数据库操作Service
* @createDate 2023-07-03 17:04:31
*/
public interface TbCheckinService extends IService<TbCheckin> {
    public String validCanCheckin(int userId,String date);

    public void checkin(HashMap hashMap );
    public void createFaceModel(int userId, MultipartFile file);

    public HashMap searchTodayCheckin(int userId);
    public long searchCheckinDays(int userId);
    public ArrayList<HashMap> searchWeekCheckin(HashMap param);
}

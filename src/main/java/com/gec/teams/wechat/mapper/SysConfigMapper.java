package com.gec.teams.wechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.teams.wechat.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sys_config】的数据库操作Mapper
* @createDate 2023-07-03 17:04:31
* @Entity com.gec.teams.wechat.entity.SysConfig
*/
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
    public List<SysConfig> selectAllParam();
}





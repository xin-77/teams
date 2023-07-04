package com.gec.teams.wechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.teams.wechat.entity.TbUser;
import org.mapstruct.Mapper;

import java.util.Set;

/**
* @author Administrator
* @description 针对表【tb_user(用户表)】的数据库操作Mapper
* @createDate 2023-07-03 17:04:31
* @Entity com.gec.teams.wechat.entity.TbUser
*/
@Mapper
public interface TbUserMapper extends BaseMapper<TbUser> {
    public boolean haveRootUser();
    public Set<String> searchUserPermissions(int userId);
}





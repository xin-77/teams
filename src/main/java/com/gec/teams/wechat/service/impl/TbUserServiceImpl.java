package com.gec.teams.wechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.teams.wechat.entity.TbUser;
import com.gec.teams.wechat.service.TbUserService;
import com.gec.teams.wechat.mapper.TbUserMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【tb_user(用户表)】的数据库操作Service实现
* @createDate 2023-07-03 17:04:31
*/
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser>
    implements TbUserService{

}





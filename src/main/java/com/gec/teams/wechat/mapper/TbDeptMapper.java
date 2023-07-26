package com.gec.teams.wechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.teams.wechat.entity.TbDept;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author Administrator
* @description 针对表【tb_dept】的数据库操作Mapper
* @createDate 2023-07-03 17:04:31
* @Entity com.gec.teams.wechat.entity.TbDept
*/
@Mapper
public interface TbDeptMapper extends BaseMapper<TbDept> {
    ArrayList<HashMap> searchDeptMembers(String keyword);
}





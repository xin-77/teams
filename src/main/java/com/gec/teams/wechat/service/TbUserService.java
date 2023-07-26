package com.gec.teams.wechat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Administrator
 * @description 针对表【tb_user(用户表)】的数据库操作Service
 * @createDate 2023-07-03 17:04:31
 */
public interface TbUserService {
    String getOpenId(String code);

    int registerUser(String registerCode, String code, String nickname, String photo);

    Set<String> searchUserPermissions(int userId);

    Integer login(String code);

    String searchUserHiredate(int userId);

    HashMap searchUserSummary(int userId);

    ArrayList<HashMap> searchUserGroupByDept(String keyword);

    ArrayList<HashMap> searchMembers(List param);
}

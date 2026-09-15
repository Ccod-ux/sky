package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * 用户业务层
 */
public interface UserService {

    /** 微信登录，新用户首次登录时自动注册 */
    User wxLogin(UserLoginDTO userLoginDTO);
}

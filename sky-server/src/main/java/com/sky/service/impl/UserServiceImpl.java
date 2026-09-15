package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户业务实现类
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN =
            "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        if (userLoginDTO == null || !StringUtils.hasText(userLoginDTO.getCode())) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        String openid = getOpenid(userLoginDTO.getCode());
        if (!StringUtils.hasText(openid)) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    /** 使用小程序临时 code 向微信换取用户 openid */
    private String getOpenid(String code) {
        if (!StringUtils.hasText(weChatProperties.getAppid())
                || !StringUtils.hasText(weChatProperties.getSecret())) {
            log.warn("微信登录配置缺失，请设置 WECHAT_APPID 和 WECHAT_SECRET");
            return null;
        }

        Map<String, String> params = new HashMap<>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(WX_LOGIN, params);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        if (!StringUtils.hasText(openid)) {
            log.warn("微信登录失败：errcode={}，errmsg={}",
                    jsonObject.getInteger("errcode"),
                    jsonObject.getString("errmsg"));
        }
        return openid;
    }
}

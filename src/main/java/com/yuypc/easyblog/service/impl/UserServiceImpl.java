package com.yuypc.easyblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuypc.easyblog.dao.entity.UserDO;
import com.yuypc.easyblog.dao.mapper.UserMapper;
import com.yuypc.easyblog.dto.req.UserLoginReqDTO;
import com.yuypc.easyblog.dto.req.UserRegisterReqDTO;
import com.yuypc.easyblog.dto.req.UserUpdateReqDTO;
import com.yuypc.easyblog.dto.resp.UserLoginRespDTO;
import com.yuypc.easyblog.dto.resp.UserRespDTO;
import com.yuypc.easyblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService{

//    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public UserRespDTO getUserByUsername(String email) {
        return null;
    }

    @Override
    public UserRespDTO getUserByEmail(String email) {
        return null;
    }

    @Override
    public Boolean hasUsername(String username) {
        return null;
    }

    @Override
    public Boolean hasEmail(String email) {
        return null;
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {

    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {

    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        return null;
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return null;
    }

    @Override
    public Void logout(String username, String token) {
        return null;
    }
}

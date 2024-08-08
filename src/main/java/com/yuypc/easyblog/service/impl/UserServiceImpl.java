package com.yuypc.easyblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuypc.easyblog.common.convention.exception.ClientException;
import com.yuypc.easyblog.dao.entity.UserDO;
import com.yuypc.easyblog.dao.mapper.UserMapper;
import com.yuypc.easyblog.dto.req.UserLoginReqDTO;
import com.yuypc.easyblog.dto.req.UserRegisterReqDTO;
import com.yuypc.easyblog.dto.req.UserUpdateReqDTO;
import com.yuypc.easyblog.dto.resp.*;
import com.yuypc.easyblog.service.UserService;
import com.yuypc.easyblog.utils.security.JwtTokenProvider;
import com.yuypc.easyblog.utils.security.PasswordUtil;
import com.yuypc.easyblog.utils.security.SaltGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import static com.yuypc.easyblog.common.constant.SecurityConstant.MD5_SALT;
import static com.yuypc.easyblog.common.convention.errcode.BaseErrorCode.*;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    @Override
    public UserRespVO getUserByUserId(Long authorId) {
        UserDO userDO = baseMapper.selectById(authorId);
        return BeanUtil.toBean(userDO, UserRespVO.class);
    }

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        UserDO user = baseMapper.selectOne(queryWrapper);
        if(user == null) {
            throw new ClientException(USER_NOT_FOUND_ERROR);
        }
        return BeanUtil.toBean(user, UserRespDTO.class);
    }


    @Override
    public Boolean hasUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Boolean hasEmail(String email) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getEmail, email);
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public UserLoginRespDTO register(UserRegisterReqDTO requestParam) {
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST_ERROR);
        }
        if (hasEmail(requestParam.getEmail())) {
            throw new ClientException(USER_EMAIL_EXIST_ERROR);
        }
        UserDO userDO = BeanUtil.toBean(requestParam, UserDO.class);
        userDO.setNickname(requestParam.getUsername());
        String rawPassword = requestParam.getPassword();
        String salt = SaltGenerator.generateSalt(MD5_SALT);
        userDO.setSalt(salt);
        String hashedPassword;
        try {
            hashedPassword = PasswordUtil.hashPassword(rawPassword, salt);
        } catch (Exception e) {
            throw new ClientException(PASSWORD_HASH_ERROR);
        }
        userDO.setPassword(hashedPassword);
        int inserted = baseMapper.insert(userDO);
        if (inserted < 1) {
            throw new ClientException(USER_SAVE_ERROR);
        }
        String token = jwtTokenProvider.createToken(requestParam.getUsername(),null);
        return UserLoginRespDTO.builder().token(token).username(userDO.getUsername()).build();
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        String currentUsername = getCurrentUsername();
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, currentUsername);
        UserDO user = baseMapper.selectOne(queryWrapper);
        user.setNickname(requestParam.getNickname());
        user.setEmail(requestParam.getEmail());
        user.setPhone(requestParam.getPhone());
        user.setBio(requestParam.getBio());
        baseMapper.updateById(user);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        String username = requestParam.getUsername();
        UserRespDTO user;
        if (username != null) {
            try {
                user=getUserByUsername(username);
            }catch (Exception e) {
                throw new ClientException(USER_NOT_FOUND_ERROR);
            }
        } else {
            throw new ClientException(USER_NOT_FOUND_ERROR);
        }
        String passwordReq = requestParam.getPassword();
        boolean verified;
        try {
            verified = PasswordUtil.verifyPassword(passwordReq, user.getSalt(), user.getPassword());
        }catch (Exception e) {
            throw new ClientException(PASSWORD_VERIFY_ERROR);
        }
        if(verified) {
            String token = jwtTokenProvider.createToken(user.getUsername(),user.getAvatarUri());
            return UserLoginRespDTO.builder().token(token).avatar(user.getAvatarUri()).username(username).build();
        }else{
            throw new ClientException(PASSWORD_INCORRECT_ERROR);
        }
    }

    @Override
    public CheckLoginRespDTO checkLogin(String token) {
        boolean b;
        try {
            b = jwtTokenProvider.validateToken(token);
        } catch (Exception e) {
            throw new ClientException(TOKEN_INVALID_ERROR);
        }
        if (b) {
            String newToken;
            try {
                newToken = jwtTokenProvider.refreshHeadToken(token);
            } catch (Exception e) {
                throw new ClientException(TOKEN_REFRESH_ERROR);
            }
            return CheckLoginRespDTO.builder()
                    .token(newToken)
                    .username(jwtTokenProvider.getUsername(token))
                    .avatar(jwtTokenProvider.getAvatar(token))
                    .build();
        }
        return null;
    }


    @Override
    public Void logout(String username, String token) {
        return null;
    }

    @Override
    public void updateAvatar(String currentUsername, String avatarUrl) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, currentUsername);
        UserDO user = baseMapper.selectOne(queryWrapper);
        user.setAvatarUri(avatarUrl);
        baseMapper.updateById(user);
    }

}


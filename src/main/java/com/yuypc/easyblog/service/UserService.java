package com.yuypc.easyblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuypc.easyblog.dao.entity.UserDO;
import com.yuypc.easyblog.dto.req.UserLoginReqDTO;
import com.yuypc.easyblog.dto.req.UserRegisterReqDTO;
import com.yuypc.easyblog.dto.req.UserUpdateReqDTO;
import com.yuypc.easyblog.dto.resp.UserLoginRespDTO;
import com.yuypc.easyblog.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查找用户信息
     * @param username
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 根据邮箱查找用户信息
     * @param email
     * @return 用户返回实体
     */
    UserRespDTO getUserByEmail(String email);

    /**
     * 查询用户名是否已存在
     * @param username
     * @return 用户名不存在返回 True，存在返回 False
     */
    Boolean hasUsername(String username);

    /**
     * 查询邮箱是否已存在
     * @param email
     * @return 邮箱不存在返回 True，存在返回 False
     */
    Boolean hasEmail(String email);

    /**
     * 注册用户
     * @param requestParam 注册用户参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 修改用户
     * @param requestParam 修改用户参数
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     * 用户登陆
     * @param requestParam 用户登陆请求参数
     * @return 用户登录响应参数
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 查看用户是否登陆
     * @param token
     * @return
     */
    Boolean checkLogin(String username,String token);

    /**
     * 用户登出
     * @param username
     * @param token
     * @return
     */
    Void logout(String username,String token);
}

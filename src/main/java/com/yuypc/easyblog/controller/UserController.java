package com.yuypc.easyblog.controller;


import com.yuypc.easyblog.common.convention.result.Result;
import com.yuypc.easyblog.common.convention.result.Results;
import com.yuypc.easyblog.dto.req.UserLoginReqDTO;
import com.yuypc.easyblog.dto.req.UserRegisterReqDTO;
import com.yuypc.easyblog.dto.req.UserUpdateReqDTO;
import com.yuypc.easyblog.dto.resp.CheckLoginRespDTO;
import com.yuypc.easyblog.dto.resp.UserLoginRespDTO;
import com.yuypc.easyblog.dto.resp.UserUpdateRespDTO;
import com.yuypc.easyblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;



/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户登陆
     * @param requestParam
     * @return
     */
    @PostMapping("/auth/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }
    /**
     * 注册用户
     *
     * @param requestParam 注册用户参数
     */
    @PostMapping("/auth/register")
    public Result<UserLoginRespDTO> register(@RequestBody UserRegisterReqDTO requestParam) {
        return Results.success(userService.register(requestParam));
    }

    /**
     * 检查用户是否登陆
     */
    @GetMapping("/auth/verify-token")
    public Result<CheckLoginRespDTO> checkLogin(@RequestParam("token") String token) {
        return Results.success(userService.checkLogin(token));
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }
}

package com.yuypc.easyblog.dto.req;

import lombok.Data;

/**
 * 用户登录请求DTO
 */
@Data
public class UserLoginReqDTO {
    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;
}

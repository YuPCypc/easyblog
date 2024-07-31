package com.yuypc.easyblog.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRespDTO {
    /**
     * 用户JWT
     */
    private String token;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatar;
}

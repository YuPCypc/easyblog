package com.yuypc.easyblog.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检查登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckLoginRespDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * token
     */
    private String token;
}

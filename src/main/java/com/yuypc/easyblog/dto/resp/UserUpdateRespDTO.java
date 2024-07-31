package com.yuypc.easyblog.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateRespDTO {
    /**
     * jwt token
     */
    private String token;
}

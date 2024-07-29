package com.yuypc.easyblog.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CaptchaVerifyReqDTO {
    /**
     * 验证码
     */
    private String captcha;
}

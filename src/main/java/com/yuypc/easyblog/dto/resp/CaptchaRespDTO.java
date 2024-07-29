package com.yuypc.easyblog.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CaptchaRespDTO {
    /**
     * 验证码图片
     */
    private String image;
}

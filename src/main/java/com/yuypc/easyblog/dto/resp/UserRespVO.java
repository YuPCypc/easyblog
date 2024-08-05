package com.yuypc.easyblog.dto.resp;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRespVO {
    /**
     * id
     */
    private Long id;

    /**
     * nickname
     */
    private String nickname;

    /**
     * email
     */
    private String email;

    /**
     * phone
     */
    private String phone;

    /**
     * bio
     */
    private String bio;

    /**
     * avatar_uri
     */
    private String avatarUri;
}

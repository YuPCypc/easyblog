package com.yuypc.easyblog.dto.resp;

import lombok.Data;

@Data
public class UserRespDTO {
    /**
     * id
     */
    private Long id;

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    /**
     * salt
     */
    private String salt;

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

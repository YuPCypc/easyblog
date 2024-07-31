package com.yuypc.easyblog.dto.req;

import lombok.Data;

@Data
public class UserRegisterReqDTO {
    /**
     * username
     */
    private String username;

    /**
     * email
     */
    private String email;

    /**
     * phone
     */
    private String phone;

    /**
     * password
     */
    private String password;
}

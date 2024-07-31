package com.yuypc.easyblog.dto.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户更新请求
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdateReqDTO {
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



}

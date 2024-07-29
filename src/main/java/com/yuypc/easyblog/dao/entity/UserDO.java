package com.yuypc.easyblog.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户实体类
 */
@Data
@TableName("user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDO {
    /**
     * id
     */
    private Long id;

    /**
     * username
     */
    private String username;

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

    /**
     * password
     */
    private String password;

    /**
     * delete_time
     */
    private Date deleteTime;

    /**
     * update_time
     */
    private Date updateTime;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * is_deleted
     */
    private Boolean isDeleted;
}

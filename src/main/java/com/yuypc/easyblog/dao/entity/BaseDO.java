package com.yuypc.easyblog.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
public class BaseDO {
    /**
     * delete_time
     */
    private Date deleteTime;

    /**
     * updated_time
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

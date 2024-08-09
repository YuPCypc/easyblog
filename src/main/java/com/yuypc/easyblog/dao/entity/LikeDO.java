package com.yuypc.easyblog.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("likes")
public class LikeDO {
    private Long userId;
    private Long articleId;
    private LocalDateTime createdAt;
    private LocalDateTime deleteTime;
    private Boolean isDeleted;
}

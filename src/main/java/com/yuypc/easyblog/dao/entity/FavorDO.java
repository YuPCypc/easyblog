package com.yuypc.easyblog.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("favorites")
public class FavorDO {
    private Long userId;
    private Long articleId;
    private LocalDateTime createdAt;
    private LocalDateTime deleteTime;
    private Boolean isDeleted;
}

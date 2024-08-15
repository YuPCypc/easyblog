package com.yuypc.easyblog.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment_likes")
public class CommentLikeDO {
    private Long userId;
    private Long commentId;
    private LocalDateTime createdAt;
    private LocalDateTime deleteTime;
    private Boolean isDeleted;
}

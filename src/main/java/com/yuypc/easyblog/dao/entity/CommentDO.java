package com.yuypc.easyblog.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@TableName("comments")
@NoArgsConstructor
@AllArgsConstructor
public class CommentDO {
    private Long id;                 // 评论ID
    private Long articleId;          // 文章ID
    private Long userId;             // 用户ID
    private String content;          // 评论内容
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
    private LocalDateTime deleteTime;// 删除时间
    private Boolean isDeleted;       // 是否已删除
    private Long rootCommentId;      // 根评论ID（针对回复的情况下）
    private Long replyCommentId;     // 回复评论的ID（如果是子评论或回复）
    private Integer commentLikeCount;// 点赞数量
    private Integer replyCommentCount; // 回复评论数量
//    private Integer floor;           // 楼层号
//    private List<CommentDO> replyComment; // 回复评论
}

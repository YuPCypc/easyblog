package com.yuypc.easyblog.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class CommentRespDTO {
    private Long id;                 // 评论ID
    private Long articleId;          // 文章ID
    private UserRespVO userRespVO;   // 用户VO
    private String content;          // 评论内容
    private LocalDateTime createdAt; // 创建时间
    private Long rootCommentId;      // 根评论ID（针对回复的情况下）
    private Long replyCommentId;     // 回复评论的ID（如果是子评论或回复）
    private Integer commentLikeCount;// 点赞数量
    private Integer replyCommentCount; // 回复评论数量(对根评论有效)
    private List<CommentRespDTO> subComments; // 子评论
}

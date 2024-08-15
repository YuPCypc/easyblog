package com.yuypc.easyblog.dto.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentAddReqDTO {
    private Long articleId;      // 文章ID
    private String content;      // 评论内容
    private Long rootCommentId;  // 根评论ID（针对回复的情况下）
    private Long replyCommentId; // 回复评论ID（针对回复的情况下）
}

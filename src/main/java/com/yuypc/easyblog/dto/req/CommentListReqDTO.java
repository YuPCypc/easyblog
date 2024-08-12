package com.yuypc.easyblog.dto.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentListReqDTO {
    /**
     * 当前页
     */
    private String currentPage;
    /**
     * 页面大小
     */
    private String pageSize;

    /**
     * 文章id
     */
    private String articleId;
}

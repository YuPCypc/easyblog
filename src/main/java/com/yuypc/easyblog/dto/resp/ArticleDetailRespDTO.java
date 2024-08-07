package com.yuypc.easyblog.dto.resp;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArticleDetailRespDTO {
    /**
     * title
     */
    private String title;

    /**
     * content
     */
    private String content;

    /**
     * 用户信息
     */
    private UserRespVO userRespVO;

    /**
     * category_id
     */
    private Long categoryId;

    /**
     * like_count
     */
    private Integer likeCount;

    /**
     * view_count
     */
    private Integer viewCount;

    /**
     * favorite_count
     */
    private Integer favoriteCount;
}

package com.yuypc.easyblog.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

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

    /**
     * update_time
     */
    private Date updateTime;

    /**
     * 是否点赞
     */
    private Boolean hasThumb;

    /**
     * 是否有收藏
     */
    private Boolean hasFavour;
}

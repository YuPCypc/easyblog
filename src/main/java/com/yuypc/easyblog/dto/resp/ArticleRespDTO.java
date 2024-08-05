package com.yuypc.easyblog.dto.resp;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Builder
@Data
public class ArticleRespDTO {
    /**
     * id
     */
    private Long id;

    /**
     * title
     */
    private String title;

    /**
     * summary
     */
    private String summary;

    /**
     * author_id
     */
    private Long authorId;

    /**
     * userVo
     */
    private UserRespVO userRespVO;

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

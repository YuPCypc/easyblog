package com.yuypc.easyblog.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@TableName("articles")
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDO extends BaseDO {
    /**
     * id
     */
    private Long id;

    /**
     * title
     */
    private String title;

    /**
     * content
     */
    private String content;

    /**
     * summary
     */
    private String summary;

    /**
     * author_id
     */
    private Long authorId;

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

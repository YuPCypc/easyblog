package com.yuypc.easyblog.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArticleUploadReqDTO {
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
     * category_id
     */
    private Long categoryId;

}

package com.yuypc.easyblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuypc.easyblog.dao.entity.ArticleDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ArticleMapper extends BaseMapper<ArticleDO> {
    @Update("UPDATE articles SET view_count = view_count + #{increment} WHERE id = #{articleId}")
    void incrementViewCountOnDB(@Param("articleId") Long articleId, @Param("increment") Long increment);

    @Update("UPDATE articles SET favorite_count = favorite_count + 1 WHERE id = #{articleId}")
    void incrementCollectCount(@Param("articleId") Long articleId);

    @Update("UPDATE articles SET like_count = like_count - 1 WHERE id = #{articleId}")
    void decrementCollectCount(@Param("articleId") Long articleId);

    @Update("UPDATE articles SET like_count = like_count + 1 WHERE id = #{articleId}")
    void incrementLikeCount(@Param("articleId") Long articleId);

    @Update("UPDATE articles SET like_count = like_count - 1 WHERE id = #{articleId}")
    void decrementLikeCount(@Param("articleId") Long articleId);
}

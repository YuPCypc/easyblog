package com.yuypc.easyblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuypc.easyblog.dao.entity.LikeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper extends BaseMapper<LikeDO> {

    @Update("UPDATE likes SET is_deleted = 0, delete_time = NULL WHERE user_id = #{userId} AND article_id = #{articleId}")
    void restoreLike(@Param("userId") Long userId, @Param("articleId") Long articleId);
}
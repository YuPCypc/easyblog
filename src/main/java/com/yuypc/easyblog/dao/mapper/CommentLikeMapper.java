package com.yuypc.easyblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuypc.easyblog.dao.entity.CommentLikeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLikeDO> {

    @Update("UPDATE comment_likes SET is_deleted = 0, delete_time = NULL WHERE user_id = #{userId} AND comment_id = #{commentId}")
    void restoreLike(@Param("userId") Long userId, @Param("commentId") Long commentId);
}

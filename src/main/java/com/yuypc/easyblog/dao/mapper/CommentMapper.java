package com.yuypc.easyblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuypc.easyblog.dao.entity.CommentDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface CommentMapper extends BaseMapper<CommentDO> {

    @Update("UPDATE comments SET comment_like_count = comment_like_count + 1 WHERE id = #{commentId}")
    void incrementLikeCount(@Param("commentId") Long commentId);

    @Update("UPDATE comments SET comment_like_count = comment_like_count - 1 WHERE id = #{commentId}")
    void decrementLikeCount(@Param("commentId") Long commentId);

    @Update("UPDATE comments SET reply_comment_count = reply_comment_count + 1 WHERE id = #{commentId}")
    void incrementCommentCount(@Param("commentId") Long commentId);

    @Update("UPDATE comments SET reply_comment_count = reply_comment_count - 1 WHERE id = #{commentId}")
    void decrementCommentCount(@Param("commentId") Long commentId);
}

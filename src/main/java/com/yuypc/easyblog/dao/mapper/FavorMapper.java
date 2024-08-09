package com.yuypc.easyblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuypc.easyblog.dao.entity.FavorDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FavorMapper extends BaseMapper<FavorDO> {

    @Update("UPDATE favorites SET is_deleted = 0, delete_time = NULL WHERE user_id = #{userId} AND article_id = #{articleId}")
    void restoreFavor(@Param("userId") Long userId, @Param("articleId") Long articleId);
}
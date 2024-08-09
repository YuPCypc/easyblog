package com.yuypc.easyblog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuypc.easyblog.dto.req.ArticleListReqDTO;
import com.yuypc.easyblog.dto.req.ArticleUploadReqDTO;
import com.yuypc.easyblog.dto.resp.ArticleDetailRespDTO;
import com.yuypc.easyblog.dto.resp.ArticleRespDTO;


public interface ArticleService {


    /**
     * 保存文章
     * @return
     */
    Void save(ArticleUploadReqDTO articleUploadReqDTO);


    /**
     * 获取文章列表
     */
    IPage<ArticleRespDTO> getMultiArticles(ArticleListReqDTO articleListReqDTO);

    /**
     * 获取文章详情
     */
    ArticleDetailRespDTO getArticleDetail(Long id);

    /**
     * 更新文章阅读量
     */
    Void incrementViewCount(Long articleId);


    /**
     * 更新文章阅读量持久化到数据库
     */
    void incrementViewCountOnDB(Long articleId, Long increment);

    /**
     * 更新文章点赞数
     */
    Void toggleLikeCount(Long articleId);

    /**
     * 更新文章收藏量
     */
    Void toggleFavorCount(Long articleId);

}

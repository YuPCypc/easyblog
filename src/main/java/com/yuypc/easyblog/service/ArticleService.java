package com.yuypc.easyblog.service;

import com.yuypc.easyblog.dto.req.ArticleListReqDTO;
import com.yuypc.easyblog.dto.req.ArticleUploadReqDTO;
import com.yuypc.easyblog.dto.resp.ArticleRespDTO;

import java.util.List;

public interface ArticleService {


    /**
     * 保存文章
     * @return
     */
    Void save(ArticleUploadReqDTO articleUploadReqDTO);


    /**
     * 获取文章列表
     */
    List<ArticleRespDTO> getMultiArticles(ArticleListReqDTO articleListReqDTO);
}

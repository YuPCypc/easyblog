package com.yuypc.easyblog.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuypc.easyblog.common.convention.result.Result;
import com.yuypc.easyblog.common.convention.result.Results;
import com.yuypc.easyblog.dto.req.ArticleListReqDTO;
import com.yuypc.easyblog.dto.req.ArticleUploadReqDTO;
import com.yuypc.easyblog.dto.resp.ArticleDetailRespDTO;
import com.yuypc.easyblog.dto.resp.ArticleRespDTO;
import com.yuypc.easyblog.service.ArticleService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class ArticleController {
    private final ArticleService articleService;

    /**
     * 保存文章
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody ArticleUploadReqDTO articleUploadReqDTO) {
        return Results.success(articleService.save(articleUploadReqDTO));
    }

    /**
     * 获取文章列表
     */
    @PostMapping("/list")
    public Result<IPage<ArticleRespDTO>> list(@RequestBody ArticleListReqDTO articleListReqDTO) {
        return Results.success(articleService.getMultiArticles(articleListReqDTO));
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleDetailRespDTO> detail(@PathVariable Long id){
        return Results.success(articleService.getArticleDetail(id));
    }

}

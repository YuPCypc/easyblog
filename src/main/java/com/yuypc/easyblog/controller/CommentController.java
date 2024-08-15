package com.yuypc.easyblog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuypc.easyblog.common.convention.result.Result;
import com.yuypc.easyblog.common.convention.result.Results;
import com.yuypc.easyblog.dto.req.CommentAddReqDTO;
import com.yuypc.easyblog.dto.req.CommentLikeReqDTO;
import com.yuypc.easyblog.dto.req.CommentListReqDTO;
import com.yuypc.easyblog.dto.resp.CommentRespDTO;
import com.yuypc.easyblog.dto.resp.UserRespDTO;
import com.yuypc.easyblog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;


    /**
     * 添加评论
     */
    @PostMapping()
    public Result<Void> add(@RequestBody CommentAddReqDTO commentAddReqDTO) {
        return Results.success(commentService.add(commentAddReqDTO));
    }

    /**
     * 获取评论列表
     */
    @PostMapping("/list")
    public Result<IPage<CommentRespDTO>> list(@RequestBody CommentListReqDTO commentListReqDTO) {
        return Results.success(commentService.getMultiComments(commentListReqDTO));
    }

    /**
     * 更新评论点赞数
     */
    @PostMapping("/like")
    public Result<Void> toggleLikeCount(@RequestBody CommentLikeReqDTO commentLikeReqDTO) {
        return Results.success(commentService.toggleLikeCount(commentLikeReqDTO));
    }

}

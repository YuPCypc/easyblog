package com.yuypc.easyblog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuypc.easyblog.dto.req.CommentAddReqDTO;
import com.yuypc.easyblog.dto.req.CommentLikeReqDTO;
import com.yuypc.easyblog.dto.req.CommentListReqDTO;
import com.yuypc.easyblog.dto.resp.CommentRespDTO;

public interface CommentService {


    Void add(CommentAddReqDTO commentAddReqDTO);

    IPage<CommentRespDTO> getMultiComments(CommentListReqDTO commentListReqDTO);

    Void toggleLikeCount(CommentLikeReqDTO commentLikeReqDTO);
}

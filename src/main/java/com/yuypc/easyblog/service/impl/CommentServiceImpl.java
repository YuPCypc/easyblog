package com.yuypc.easyblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuypc.easyblog.dao.entity.ArticleDO;
import com.yuypc.easyblog.dao.entity.CommentDO;
import com.yuypc.easyblog.dao.mapper.ArticleMapper;
import com.yuypc.easyblog.dao.mapper.CommentMapper;
import com.yuypc.easyblog.dto.req.CommentAddReqDTO;
import com.yuypc.easyblog.dto.req.CommentListReqDTO;
import com.yuypc.easyblog.dto.resp.CommentRespDTO;
import com.yuypc.easyblog.dto.resp.UserRespVO;
import com.yuypc.easyblog.service.CommentService;
import com.yuypc.easyblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.Integer.parseInt;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentDO> implements CommentService {

    private final ArticleMapper articleMapper;

    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void add(CommentAddReqDTO commentAddReqDTO) {
        // 添加评论
        // 1. 将 CommentAddReqDTO 转换为 CommentDO
        Long rootCommentId = 0L;
        if (commentAddReqDTO.getRootCommentId() != null) {
            rootCommentId = commentAddReqDTO.getRootCommentId();
        }
        String currentUsername = userService.getCurrentUsername();
        Long currentUserId = userService.getUserByUsername(currentUsername).getId();
        CommentDO commentDO = CommentDO.builder()
                .articleId(commentAddReqDTO.getArticleId())
                .content(commentAddReqDTO.getContent())
                .userId(currentUserId)
                .rootCommentId(rootCommentId)
                .build();
        // 2. 保存评论
        baseMapper.insert(commentDO);

        // 3. 更新对应文章的评论数
        articleMapper.incrementCommentCount(commentDO.getArticleId());

        return null;
    }

    @Override
    public IPage<CommentRespDTO> getMultiComments(CommentListReqDTO commentListReqDTO) {

        String articleId = commentListReqDTO.getArticleId();
        // 查询评论
        LambdaQueryWrapper<CommentDO> queryWrapper = Wrappers.lambdaQuery(CommentDO.class)
                .eq(CommentDO::getIsDeleted, false)
                .eq(CommentDO::getRootCommentId, 0L)
                .eq(CommentDO::getArticleId, articleId)
                .orderByDesc(CommentDO::getCreatedAt);
        int currentPage = parseInt(commentListReqDTO.getCurrentPage());
        int pageSize = parseInt(commentListReqDTO.getPageSize());
        Page<CommentDO> page = new Page<>(currentPage, pageSize);
        IPage<CommentDO> commentDOPage = baseMapper.selectPage(page, queryWrapper);

        return commentDOPage.convert(commentDO -> {
            // 查询子评论
            LambdaQueryWrapper<CommentDO> queryWrapper2 = Wrappers.lambdaQuery(CommentDO.class)
                    .eq(CommentDO::getIsDeleted, false)
                    .eq(CommentDO::getRootCommentId, commentDO.getId())
                    .orderByDesc(CommentDO::getCreatedAt)
                    .last("limit 3");
            List<CommentDO> childCommentDOList = baseMapper.selectList(queryWrapper2);

            List<CommentRespDTO> commentRespDTOS = childCommentDOList.stream().map(commentDO1 -> {
                CommentRespDTO commentRespDTO = CommentRespDTO.builder().build();
                BeanUtil.copyProperties(commentDO1, commentRespDTO);
                return commentRespDTO;
            }).toList();


            Long userId = commentDO.getUserId();
            UserRespVO userRespVO = userService.getUserByUserId(userId);
            CommentRespDTO commentRespDTO = CommentRespDTO.builder()
                    .id(commentDO.getId())
                    .articleId(commentDO.getArticleId())
                    .userRespVO(userRespVO)
                    .content(commentDO.getContent())
                    .commentLikeCount(commentDO.getCommentLikeCount())
                    .replyCommentCount(commentDO.getReplyCommentCount())
                    .createdAt(commentDO.getCreatedAt())
                    .subComments(commentRespDTOS)
                    .rootCommentId(commentDO.getRootCommentId())
                    .build();
            return commentRespDTO;
        });
    }
}

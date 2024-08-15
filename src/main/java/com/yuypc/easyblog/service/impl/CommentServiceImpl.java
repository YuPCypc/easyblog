package com.yuypc.easyblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuypc.easyblog.dao.entity.CommentDO;
import com.yuypc.easyblog.dao.entity.CommentLikeDO;
import com.yuypc.easyblog.dao.mapper.ArticleMapper;
import com.yuypc.easyblog.dao.mapper.CommentLikeMapper;
import com.yuypc.easyblog.dao.mapper.CommentMapper;
import com.yuypc.easyblog.dto.req.CommentAddReqDTO;
import com.yuypc.easyblog.dto.req.CommentLikeReqDTO;
import com.yuypc.easyblog.dto.req.CommentListReqDTO;
import com.yuypc.easyblog.dto.resp.CommentRespDTO;
import com.yuypc.easyblog.dto.resp.UserRespVO;
import com.yuypc.easyblog.service.CommentService;
import com.yuypc.easyblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Integer.parseInt;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentDO> implements CommentService {

    private final ArticleMapper articleMapper;

    private final UserService userService;

    private final CommentLikeMapper commentLikeMapper;

    private final CommentMapper commentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void add(CommentAddReqDTO commentAddReqDTO) {
        // 添加评论
        // 1. 将 CommentAddReqDTO 转换为 CommentDO
        Long rootCommentId = 0L;
        if (commentAddReqDTO.getRootCommentId() != null) {
            rootCommentId = commentAddReqDTO.getRootCommentId();
        }
        if(commentAddReqDTO.getReplyCommentId()!=-1){
            commentMapper.incrementCommentCount(commentAddReqDTO.getReplyCommentId());
        }else{
            commentAddReqDTO.setReplyCommentId(0L);
        }
        Long currentUserId = userService.getCurrentUserId();
        CommentDO commentDO = CommentDO.builder()
                .articleId(commentAddReqDTO.getArticleId())
                .content(commentAddReqDTO.getContent())
                .userId(currentUserId)
                .rootCommentId(rootCommentId)
                .replyCommentId(commentAddReqDTO.getReplyCommentId())
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
                    .orderByDesc(CommentDO::getCreatedAt);
            List<CommentDO> childCommentDOList = baseMapper.selectList(queryWrapper2);

            List<CommentRespDTO> commentRespDTOS = childCommentDOList.stream().map(commentDO1 -> {
                // 查询子评论的用户信息
                Long userId = commentDO1.getUserId();
                UserRespVO userRespVO = userService.getUserByUserId(userId);
                CommentRespDTO commentRespDTO = CommentRespDTO.builder().build();
                BeanUtil.copyProperties(commentDO1, commentRespDTO);
                commentRespDTO.setUserRespVO(userRespVO);
                return commentRespDTO;
            }).toList();


            Long userId = commentDO.getUserId();
            UserRespVO userRespVO = userService.getUserByUserId(userId);
            CommentRespDTO commentRespDTO = CommentRespDTO.builder()
                    .id(commentDO.getId().toString())
                    .articleId(commentDO.getArticleId().toString())
                    .userRespVO(userRespVO)
                    .content(commentDO.getContent())
                    .commentLikeCount(commentDO.getCommentLikeCount())
                    .replyCommentCount(commentDO.getReplyCommentCount())
                    .createdAt(commentDO.getCreatedAt())
                    .subComments(commentRespDTOS)
                    .rootCommentId(commentDO.getRootCommentId().toString())
                    .build();
            return commentRespDTO;
        });
    }

    @Override
    public Void toggleLikeCount(CommentLikeReqDTO commentLikeReqDTO) {
        String commentId1 = commentLikeReqDTO.getCommentId();
        Long commentId = Long.parseLong(commentId1);

        Long userId = userService.getCurrentUserId();

        LambdaQueryWrapper<CommentLikeDO> queryWrapper = Wrappers.lambdaQuery(CommentLikeDO.class)
                .eq(CommentLikeDO::getUserId, userId)
                .eq(CommentLikeDO::getCommentId, commentId);

        CommentLikeDO commentLikeDO = commentLikeMapper.selectOne(queryWrapper);

        if (commentLikeDO == null) {
            // 如果用户没有点赞记录，插入一条新记录
            CommentLikeDO newLike = new CommentLikeDO();
            newLike.setUserId(userId);
            newLike.setCommentId(commentId);
            newLike.setIsDeleted(false);
            commentLikeMapper.insert(newLike);

            // 更新文章的点赞计数
            baseMapper.incrementLikeCount(commentId);
        } else if (commentLikeDO.getIsDeleted()) {
            // 如果用户已经点赞但被标记为删除，恢复点赞
            commentLikeMapper.restoreLike(userId, commentId);

            // 更新文章的点赞计数
            baseMapper.incrementLikeCount(commentId);
        } else {
            // 用户已经点赞且未删除，执行取消点赞
            commentLikeDO.setIsDeleted(true);
            commentLikeDO.setDeleteTime(LocalDateTime.now());
            // 使用条件更新而不是根据 ID 更新
            LambdaUpdateWrapper<CommentLikeDO> updateWrapper = Wrappers.lambdaUpdate(CommentLikeDO.class)
                    .eq(CommentLikeDO::getUserId, userId)
                    .eq(CommentLikeDO::getCommentId, commentId)
                    .set(CommentLikeDO::getIsDeleted, true)
                    .set(CommentLikeDO::getDeleteTime, LocalDateTime.now());

            commentLikeMapper.update(commentLikeDO, updateWrapper);

            // 减少文章的点赞计数
            baseMapper.decrementLikeCount(commentId);
        }

        return null;
    }
}

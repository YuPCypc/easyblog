package com.yuypc.easyblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuypc.easyblog.common.convention.exception.ServiceException;
import com.yuypc.easyblog.dao.entity.ArticleDO;
import com.yuypc.easyblog.dao.mapper.ArticleMapper;
import com.yuypc.easyblog.dto.req.ArticleListReqDTO;
import com.yuypc.easyblog.dto.req.ArticleUploadReqDTO;
import com.yuypc.easyblog.dto.resp.ArticleRespDTO;
import com.yuypc.easyblog.dto.resp.UserRespDTO;
import com.yuypc.easyblog.dto.resp.UserRespVO;
import com.yuypc.easyblog.service.ArticleService;
import com.yuypc.easyblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.yuypc.easyblog.common.convention.errcode.BaseErrorCode.ARTICLE_SAVE_ERROR;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleDO> implements ArticleService {
    @Autowired
    private UserService userService;

    @Override
    public Void save(ArticleUploadReqDTO articleUploadReqDTO) {
        // 获取当前用户信息
        String username = userService.getCurrentUsername();
        UserRespDTO user = userService.getUserByUsername(username);
        ArticleDO articleDO = ArticleDO.builder().
                title(articleUploadReqDTO.getTitle()).
                content(articleUploadReqDTO.getContent()).
                summary(articleUploadReqDTO.getSummary()).
                authorId(user.getId()).
                categoryId(articleUploadReqDTO.getCategoryId()).
                likeCount(0).
                viewCount(0).
                favoriteCount(0).
                build();
        int insert = baseMapper.insert(articleDO);
        if (insert == 0) {
            throw new ServiceException(ARTICLE_SAVE_ERROR);
        }
        return null;
    }

    private UserRespVO getUserRespVOById(Long authorId) {
        return userService.getUserByUserId(authorId);
    }

    @Override
    public List<ArticleRespDTO> getMultiArticles(ArticleListReqDTO articleListReqDTO) {
        LambdaQueryWrapper<ArticleDO> queryWrapper;
        String categoryId = articleListReqDTO.getCategoryId();
        int categoryIdInt = Integer.parseInt(categoryId);
        categoryIdInt -= 1;
        if (categoryIdInt > 0) {
            queryWrapper = Wrappers.lambdaQuery(ArticleDO.class).eq(ArticleDO::getCategoryId, categoryIdInt);
        } else {
            queryWrapper = Wrappers.lambdaQuery(ArticleDO.class);
        }
        List<ArticleDO> articleDOList = baseMapper.selectList(queryWrapper);
        return articleDOList.stream()
                .map(articleDO -> ArticleRespDTO.builder()
                        .id(articleDO.getId())
                        .title(articleDO.getTitle())
                        .summary(articleDO.getSummary())
                        .authorId(articleDO.getAuthorId())
                        .userRespVO(getUserRespVOById(articleDO.getAuthorId())) // 获取 UserRespVO
                        .likeCount(articleDO.getLikeCount())
                        .viewCount(articleDO.getViewCount())
                        .favoriteCount(articleDO.getFavoriteCount())
                        .build())
                .collect(Collectors.toList());
    }
}

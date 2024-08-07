package com.yuypc.easyblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuypc.easyblog.common.convention.exception.ClientException;
import com.yuypc.easyblog.common.convention.exception.ServiceException;
import com.yuypc.easyblog.dao.entity.ArticleDO;
import com.yuypc.easyblog.dao.mapper.ArticleMapper;
import com.yuypc.easyblog.dto.req.ArticleListReqDTO;
import com.yuypc.easyblog.dto.req.ArticleUploadReqDTO;
import com.yuypc.easyblog.dto.resp.ArticleDetailRespDTO;
import com.yuypc.easyblog.dto.resp.ArticleRespDTO;
import com.yuypc.easyblog.dto.resp.UserRespDTO;
import com.yuypc.easyblog.dto.resp.UserRespVO;
import com.yuypc.easyblog.service.ArticleService;
import com.yuypc.easyblog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;


import static com.yuypc.easyblog.common.convention.errcode.BaseErrorCode.ARTICLE_NOT_EXIST_ERROR;
import static com.yuypc.easyblog.common.convention.errcode.BaseErrorCode.ARTICLE_SAVE_ERROR;
import static java.lang.Integer.parseInt;

@Service
@Slf4j
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
    public IPage<ArticleRespDTO> getMultiArticles(ArticleListReqDTO articleListReqDTO) {
        LambdaQueryWrapper<ArticleDO> queryWrapper;
        String categoryId = articleListReqDTO.getCategoryId();
        int categoryIdInt = parseInt(categoryId);
        categoryIdInt -= 1;
        if (categoryIdInt > 0) {
            queryWrapper = Wrappers.lambdaQuery(ArticleDO.class).eq(ArticleDO::getCategoryId, categoryIdInt);
        } else {
            queryWrapper = Wrappers.lambdaQuery(ArticleDO.class);
        }
        int currentPage = parseInt(articleListReqDTO.getCurrentPage());
        int pageSize = parseInt(articleListReqDTO.getPageSize());
        Page<ArticleDO> page = new Page<>(currentPage, pageSize);
        IPage<ArticleDO> articleDOList = baseMapper.selectPage(page, queryWrapper);
        return articleDOList.convert(
                articleDO -> {
                    String value = String.valueOf(articleDO.getId());
                    ArticleRespDTO articleRespDTOInner =
                            ArticleRespDTO.builder()
                                    .id(value)
                                    .title(articleDO.getTitle())
                                    .summary(articleDO.getSummary())
                                    .authorId(articleDO.getAuthorId())
                                    .userRespVO(getUserRespVOById(articleDO.getAuthorId())) // 获取 UserRespVO
                                    .likeCount(articleDO.getLikeCount())
                                    .viewCount(articleDO.getViewCount())
                                    .favoriteCount(articleDO.getFavoriteCount())
                                    .build();
                    return articleRespDTOInner;
                });
    }

    @Override
    public ArticleDetailRespDTO getArticleDetail(Long id) {
        ArticleDO articleDO = baseMapper.selectById(id);
        if (articleDO == null) {
            throw new ClientException(ARTICLE_NOT_EXIST_ERROR);
        }
        ArticleDetailRespDTO articleDetailRespDTOInner = ArticleDetailRespDTO.builder()
                .title(articleDO.getTitle())
                .content(articleDO.getContent())
                .userRespVO(getUserRespVOById(articleDO.getAuthorId())) // 获取 UserRespVO
                .likeCount(articleDO.getLikeCount())
                .viewCount(articleDO.getViewCount())
                .favoriteCount(articleDO.getFavoriteCount())
                .updateTime(articleDO.getUpdateTime())
                .categoryId(articleDO.getCategoryId())
                .build();
        return articleDetailRespDTOInner;
    }
}

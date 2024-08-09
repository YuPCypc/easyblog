package com.yuypc.easyblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuypc.easyblog.common.convention.exception.ClientException;
import com.yuypc.easyblog.common.convention.exception.ServiceException;
import com.yuypc.easyblog.dao.entity.ArticleDO;
import com.yuypc.easyblog.dao.entity.FavorDO;
import com.yuypc.easyblog.dao.entity.LikeDO;
import com.yuypc.easyblog.dao.mapper.ArticleMapper;
import com.yuypc.easyblog.dao.mapper.FavorMapper;
import com.yuypc.easyblog.dao.mapper.LikeMapper;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.yuypc.easyblog.common.constant.RedisCacheConstant.ARTICLE_VIEW_KEY_PREFIX;
import static com.yuypc.easyblog.common.convention.errcode.BaseErrorCode.ARTICLE_NOT_EXIST_ERROR;
import static com.yuypc.easyblog.common.convention.errcode.BaseErrorCode.ARTICLE_SAVE_ERROR;
import static java.lang.Integer.parseInt;

@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleDO> implements ArticleService {
    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private FavorMapper favorMapper;

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
        String currentUsername = userService.getCurrentUsername();
        UserRespDTO user = userService.getUserByUsername(currentUsername);
        Long userId = user.getId();

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
        // 查询用户是否已经点赞
        LambdaQueryWrapper<LikeDO> queryWrapper = Wrappers.lambdaQuery(LikeDO.class)
                .eq(LikeDO::getUserId, userId)
                .eq(LikeDO::getArticleId, id);
        LikeDO likeRecord = likeMapper.selectOne(queryWrapper);
        if (likeRecord != null&& !likeRecord.getIsDeleted()) {
            articleDetailRespDTOInner.setHasThumb(true);
        }

        // 查询用户是否已收藏
        LambdaQueryWrapper<FavorDO> queryWrapper1 = Wrappers.lambdaQuery(FavorDO.class)
                .eq(FavorDO::getUserId, userId)
                .eq(FavorDO::getArticleId, id);
        FavorDO favorRecord = favorMapper.selectOne(queryWrapper1);
        if (favorRecord != null&& !favorRecord.getIsDeleted()) {
            articleDetailRespDTOInner.setHasFavour(true);
        }

        return articleDetailRespDTOInner;
    }

    @Override
    public Void incrementViewCount(Long articleId) {
        String redisKey = ARTICLE_VIEW_KEY_PREFIX + articleId;
        redisTemplate.opsForValue().increment(redisKey);
        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCountOnDB(Long articleId, Long viewIncrement){
        baseMapper.incrementViewCountOnDB(articleId, viewIncrement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void toggleLikeCount(Long articleId) {
        String currentUsername = userService.getCurrentUsername();
        UserRespDTO user = userService.getUserByUsername(currentUsername);
        Long userId = user.getId();

        // 查询用户是否已经点赞
        LambdaQueryWrapper<LikeDO> queryWrapper = Wrappers.lambdaQuery(LikeDO.class)
                .eq(LikeDO::getUserId, userId)
                .eq(LikeDO::getArticleId, articleId);

        LikeDO likeRecord = likeMapper.selectOne(queryWrapper);

        if (likeRecord == null) {
            // 如果用户没有点赞记录，插入一条新记录
            LikeDO newLike = new LikeDO();
            newLike.setUserId(userId);
            newLike.setArticleId(articleId);
            newLike.setIsDeleted(false);
            likeMapper.insert(newLike);

            // 更新文章的点赞计数
            baseMapper.incrementLikeCount(articleId);
        } else if (likeRecord.getIsDeleted()) {
            // 如果用户已经点赞但被标记为删除，恢复点赞
            likeMapper.restoreLike(userId, articleId);

            // 更新文章的点赞计数
            baseMapper.incrementLikeCount(articleId);
        } else {
            // 用户已经点赞且未删除，执行取消点赞
            likeRecord.setIsDeleted(true);
            likeRecord.setDeleteTime(LocalDateTime.now());
            // 使用条件更新而不是根据 ID 更新
            LambdaUpdateWrapper<LikeDO> updateWrapper = Wrappers.lambdaUpdate(LikeDO.class)
                    .eq(LikeDO::getUserId, userId)
                    .eq(LikeDO::getArticleId, articleId)
                    .set(LikeDO::getIsDeleted, true)
                    .set(LikeDO::getDeleteTime, LocalDateTime.now());

            likeMapper.update(likeRecord, updateWrapper);

            // 减少文章的点赞计数
            baseMapper.decrementLikeCount(articleId);
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void toggleFavorCount(Long articleId) {
        String currentUsername = userService.getCurrentUsername();
        UserRespDTO user = userService.getUserByUsername(currentUsername);
        Long userId = user.getId();

        // 查询用户是否已经收藏
        LambdaQueryWrapper<FavorDO> queryWrapper = Wrappers.lambdaQuery(FavorDO.class)
                .eq(FavorDO::getUserId, userId)
                .eq(FavorDO::getArticleId, articleId);

        FavorDO favorRecord = favorMapper.selectOne(queryWrapper);

        if (favorRecord == null) {
            // 如果用户没有收藏记录，插入一条新记录
            FavorDO newFavor = new FavorDO();
            newFavor.setUserId(userId);
            newFavor.setArticleId(articleId);
            newFavor.setIsDeleted(false);
            favorMapper.insert(newFavor);

            // 更新文章的收藏计数
            baseMapper.incrementCollectCount(articleId);
        } else if (favorRecord.getIsDeleted()) {
            // 如果用户已经点赞但被标记为删除，恢复点赞
            favorMapper.restoreFavor(userId, articleId);

            // 更新文章的收藏计数
            baseMapper.incrementCollectCount(articleId);
        } else {
            // 用户已经收藏且未删除，执行取消收藏
            favorRecord.setIsDeleted(true);
            favorRecord.setDeleteTime(LocalDateTime.now());
            // 使用条件更新而不是根据 ID 更新
            LambdaUpdateWrapper<FavorDO> updateWrapper = Wrappers.lambdaUpdate(FavorDO.class)
                    .eq(FavorDO::getUserId, userId)
                    .eq(FavorDO::getArticleId, articleId)
                    .set(FavorDO::getIsDeleted, true)
                    .set(FavorDO::getDeleteTime, LocalDateTime.now());

            favorMapper.update(favorRecord, updateWrapper);

            // 减少文章的收藏计数
            baseMapper.decrementCollectCount(articleId);
        }

        return null;
    }
}

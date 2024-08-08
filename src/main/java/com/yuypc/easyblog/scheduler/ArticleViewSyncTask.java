package com.yuypc.easyblog.scheduler;

import com.yuypc.easyblog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.yuypc.easyblog.common.constant.RedisCacheConstant.ARTICLE_VIEW_KEY_PREFIX;

@Component
public class ArticleViewSyncTask {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ArticleService articleService;


    //    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨 3 点执行
    @Scheduled(fixedRate = 300000) // 每隔 5 分钟执行一次
    public void syncArticleViewCountToDatabase() {
        Set<String> keys = redisTemplate.keys(ARTICLE_VIEW_KEY_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                Long articleId = Long.valueOf(key.replace(ARTICLE_VIEW_KEY_PREFIX, ""));
                Long viewIncrement = Long.valueOf(redisTemplate.opsForValue().get(key));

                // 增量更新数据库中的 viewCount
                articleService.incrementViewCountOnDB(articleId, viewIncrement);

                // 删除已同步的 Redis 键
                redisTemplate.delete(key);
            }
        }
    }
}

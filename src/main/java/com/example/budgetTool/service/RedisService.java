package com.example.budgetTool.service;

import com.example.budgetTool.config.RedisConfig;
import com.example.budgetTool.repository.RedisRepository;
import com.example.budgetTool.utils.RedisUtil;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * packageName    : com.example.budgetTool.service
 * author         : kimjaehyeong
 * date           : 11/21/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/21/25        kimjaehyeong       created
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService implements RedisRepository {
    private final RedisUtil redisUtil;
    private final RedisConfig redisConfig;

    /**
     * Setting single redis data pair.
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public int setSingleData(String key, Object value) {
        return this.redisUtil.executeOperation(() -> this.redisUtil.getValueOperations().set(key, value));
    }

    /**
     * Setting single redis data pair.
     *
     * @param key
     * @param value
     * @param duration
     * @return
     */
    @Override
    public int setSingleData(String key, Object value, Duration duration) {
        return this.redisUtil.executeOperation(() -> this.redisUtil.getValueOperations().set(key, value, duration));
    }

    /**
     * geting single redis data pair.
     *
     * @param key
     * @return
     */
    @Override
    public String getSingleData(String key) {
        if (this.redisUtil.getValueOperations().get(key) == null) return null;
        return String.valueOf(this.redisUtil.getValueOperations().get(key));
    }

    /**
     * deleting single data pair.
     *
     * @param key
     * @return
     */
    @Override
    public int deleteSingleData(String key) {
        if (this.redisUtil.getValueOperations().get(key) == null) return 0;

        return this.redisUtil.executeOperation(() -> this.redisConfig.getRedisTemplate().delete(key));
    }
}

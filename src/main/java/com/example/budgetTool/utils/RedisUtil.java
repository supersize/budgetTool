package com.example.budgetTool.utils;

import com.example.budgetTool.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.example.budgetTool.utils
 * author         : kimjaehyeong
 * date           : 11/21/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/21/25        kimjaehyeong       created
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisConfig redisConfig;

    /**
     * getting info of list in Redis
     *
     * @return ListOperations
     */
    public ListOperations<String, Object> getListOperations() {
        return this.redisConfig.getRedisTemplate().opsForList();
    }

    /**
     * getting info of values in Redis
     *
     * @return ValueOperations
     */
    public ValueOperations<String, Object> getValueOperations() {
        return this.redisConfig.getRedisTemplate().opsForValue();
    }

    /**
     * Handling the processing and exception handling for CRUD operations in Redis.
     *
     * @param runnable
     * @return int
     */
    public int executeOperation(Runnable runnable) {
        try {
            runnable.run();
            return 1;
        } catch (Exception e) {
            log.error("[error] redis processing...", e);
            return 0;
        }
    }


}



package com.example.budgetTool.repository;

import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * packageName    : com.example.budgetTool.repository
 * author         : kimjaehyeong
 * date           : 11/21/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/21/25        kimjaehyeong       created
 */
@Repository
public interface RedisRepository {

    int setSingleData(String key, Object value);

    int setSingleData(String key, Object value, Duration duration);

    String getSingleData(String key);

    int deleteSingleData(String key);
}

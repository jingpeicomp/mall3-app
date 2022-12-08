package com.bik.web3.mall3.auth.session;

import com.bik.web3.mall3.common.utils.HostUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;

/**
 * 定时清理用户token信息(每天执行一次)
 *
 * @author Mingo.Liu
 */
@RequiredArgsConstructor
public class SessionScheduler {

    private final StringRedisTemplate redisTemplate;

    private final SessionService sessionService;

    /**
     * 每天凌晨4点20分执行用户token数据清理
     */
    @Scheduled(cron = "0 20 4 * * ?")
    public void clean() {
        if (getLock()) {
            sessionService.cleanUserTokens();
        }
    }

    private boolean getLock() {
        long dayStamp = System.currentTimeMillis() / (1000 * 3600 * 24);
        String lockKey = "SessionCleanLocker_" + dayStamp;
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, HostUtils.getLocalIp(), Duration.ofSeconds(7200)));
    }
}

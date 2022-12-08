package com.bik.web3.mall3.auth.session;

import com.bik.web3.mall3.auth.configure.AuthProperties;
import com.bik.web3.mall3.auth.configure.RedisTemplateFactory;
import com.bik.web3.mall3.auth.login.dto.LoginUser;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * session redis操作集合
 *
 * @author Mingo.Liu
 */
@RequiredArgsConstructor
@Slf4j
public class SessionRedisOperations {

    private final AuthProperties authProperties;

    private final RedisTemplateFactory redisTemplateFactory;

    private StringRedisTemplate redisTemplate;

    private final String tokenPrefix = "token_";
    private final String userTokenPrefix = "userToken_";

    /**
     * 创建Session
     *
     * @param token     token
     * @param loginUser 用户信息
     */
    public void create(String token, LoginUser loginUser) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            stringRedisConn.set(getSessionKey(token), ObjectUtils.toJson(loginUser),
                    Expiration.seconds(authProperties.getSessionAliveSeconds()), RedisStringCommands.SetOption.UPSERT);
            stringRedisConn.sAdd(getUserSessionKey(loginUser.getUserId()), token);
            return null;
        });
    }

    /**
     * 更新session
     *
     * @param token     token
     * @param loginUser 用户信息
     */
    public void update(String token, LoginUser loginUser) {
        redisTemplate.opsForValue().setIfPresent(getSessionKey(token), ObjectUtils.toJson(loginUser),
                Duration.ofSeconds(authProperties.getSessionAliveSeconds()));
    }

    /**
     * 刷新session有效期
     *
     * @param token token
     */
    public void refresh(String token) {
        LoginUser loginUser = get(token);
        if (null != loginUser) {
            loginUser.setRefreshTime(LocalDateTime.now());
            redisTemplate.opsForValue().setIfPresent(getSessionKey(token), ObjectUtils.toJson(loginUser),
                    Duration.ofSeconds(authProperties.getSessionAliveSeconds()));
        }
    }

    /**
     * 获取session 用户
     *
     * @param token token
     * @return session用户信息
     */
    public LoginUser get(String token) {
        String json = redisTemplate.opsForValue().get(getSessionKey(token));
        return ObjectUtils.fromJson(json, LoginUser.class);
    }

    /**
     * 根据token清除指定的session信息
     *
     * @param userId 用户ID
     * @param token  token
     */
    public void remove(Long userId, String token) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            stringRedisConn.del(getSessionKey(token));
            stringRedisConn.sRem(getUserSessionKey(userId), token);
            return null;
        });
    }

    /**
     * 根据token清除指定的session信息
     *
     * @param token token
     */
    public void remove(String token) {
        LoginUser loginUser = get(token);
        if (null != loginUser) {
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                stringRedisConn.del(getSessionKey(token));
                stringRedisConn.sRem(getUserSessionKey(loginUser.getUserId()), token);
                return null;
            });
        }
    }

    /**
     * 清除用户所有的session
     *
     * @param userId 用户ID
     */
    public void removeUser(Long userId) {
        String userSessionKey = getUserSessionKey(userId);
        Set<String> userTokens = redisTemplate.opsForSet().members(userSessionKey);
        if (CollectionUtils.isNotEmpty(userTokens)) {
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                userTokens.forEach(token -> stringRedisConn.del(getSessionKey(token)));
                return null;
            });
        }
        redisTemplate.delete(userSessionKey);
    }

    /**
     * 获取系统所有已登陆用户
     *
     * @return 已登录用户ID列表
     */
    public List<Long> queryAllLoggedUsers() {
        Set<String> userTokenKeys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            Cursor<byte[]> cursor = stringRedisConn.scan(ScanOptions.scanOptions().match(userTokenPrefix + "*").count(200).build());
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
            return keys;
        });

        if (CollectionUtils.isEmpty(userTokenKeys)) {
            return Collections.emptyList();
        }

        return userTokenKeys.stream()
                .map(key -> NumberUtils.toLong(key.substring(userTokenPrefix.length()), -1))
                .filter(id -> id >= 0)
                .collect(Collectors.toList());
    }

    /**
     * 清理指定用户无效的token
     *
     * @param userId 用户ID
     */
    public void cleanUserToken(Long userId) {
        String userSessionKey = getUserSessionKey(userId);
        Set<String> userTokenSet = redisTemplate.opsForSet().members(userSessionKey);
        if (CollectionUtils.isEmpty(userTokenSet)) {
            return;
        }

        List<String> userTokens = new ArrayList<>(userTokenSet);
        List<Object> existResults = redisTemplate.executePipelined((RedisCallback<?>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            userTokens.forEach(token -> stringRedisConn.exists(getSessionKey(token)));
            return null;
        });

        List<String> needRemovedTokens = new ArrayList<>();
        for (int i = 0; i < existResults.size(); i++) {
            if (Boolean.FALSE.equals(existResults.get(i))) {
                needRemovedTokens.add(userTokens.get(i));
            }
        }
        if (CollectionUtils.isNotEmpty(needRemovedTokens)) {
            log.info("User token is invalid, will be removed {}", needRemovedTokens);
            redisTemplate.opsForSet().remove(userSessionKey, needRemovedTokens);
        }
    }

    /**
     * token->user redis key
     *
     * @param token token
     * @return redis key
     */
    private String getSessionKey(String token) {
        return tokenPrefix + token;
    }

    /**
     * user->[token1,token2] redis key
     *
     * @param userId 用户ID
     * @return 用户token列表 redis key
     */
    private String getUserSessionKey(Long userId) {
        return userTokenPrefix + userId;
    }

    @PostConstruct
    public void init() {
        redisTemplate = redisTemplateFactory.getRedisTemplate();
    }
}

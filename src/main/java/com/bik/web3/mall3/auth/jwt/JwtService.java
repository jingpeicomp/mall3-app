package com.bik.web3.mall3.auth.jwt;

import com.bik.web3.mall3.auth.configure.AuthProperties;
import com.bik.web3.mall3.auth.login.dto.LoginUser;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt 接口
 *
 * @author Mingo.Liu
 */
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final AuthProperties authProperties;

    private SecretKey key;

    /**
     * 生成jwt token
     *
     * @param payload jwt body
     * @return jwt token
     */
    public String generateToken(Map<String, Object> payload) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(payload)
                .setIssuedAt(now)
                .setExpiration(DateUtils.addSeconds(now, authProperties.getJwt().getExpiredSeconds()))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 生成jwt token
     *
     * @param user 登陆用户
     * @return jwt token
     */
    public String generateToken(LoginUser user) {
        Map<String, Object> payload = new HashMap<>(4);
        payload.put("userId", user.getUserId());
        payload.put("loginTime", user.getLoginTime());
        return generateToken(payload);
    }

    /**
     * 获取jwt token body
     *
     * @param token jwt token
     * @return jwt消息体
     */
    public Map<String, Object> getPayload(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException jwtException) {
            throw new Mall3Exception(ResultCodes.EXPIRED_TOKEN);
        } catch (JwtException jwtException) {
            throw new Mall3Exception(ResultCodes.INVALID_TOKEN);
        }
    }

    /**
     * 从token解析登陆用户信息
     *
     * @param token jwt token
     * @return token中携带的登陆用户信息
     */
    public LoginUser parseLoginUser(String token) {
        Map<String, Object> payload = getPayload(token);
        return ObjectUtils.fromMap(payload, LoginUser.class);
    }

    /**
     * 检查token是否有效
     *
     * @param token jwt token
     * @return 有效返回true，反之false
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean validateToken(String token) {
        try {
            getPayload(token);
            return true;
        } catch (ExpiredJwtException jwtException) {
            log.error("Jwt token expired {}", token);
            return false;
        } catch (JwtException jwtException) {
            log.error("Jwt token invalid {}", token);
            return false;
        }
    }

    @PostConstruct
    public void init() {
        byte[] bytes = Decoders.BASE64.decode(authProperties.getJwt().getSecretKey());
        key = new SecretKeySpec(bytes, SignatureAlgorithm.HS512.getJcaName());
    }
}

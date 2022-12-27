package com.bik.web3.mall3.domain.user;

import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.bean.user.request.BindUserInfoRequest;
import com.bik.web3.mall3.bean.user.request.BindWeb3AddressRequest;
import com.bik.web3.mall3.bean.user.request.VerifyPasswordRequest;
import com.bik.web3.mall3.bean.user.request.VerifyWeb3AddressRequest;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.common.utils.BCrypt;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import com.bik.web3.mall3.common.utils.generator.UuidGenerator;
import com.bik.web3.mall3.web3.Web3Operations;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户领域服务
 *
 * @author Mingo.Liu
 * @date 2022-12-07
 */
@Service
@CacheConfig(cacheNames = "cacheUser")
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final Web3Operations web3Operations;

    private final StringRedisTemplate redisTemplate;

    /**
     * 根据ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户详情
     */
//    @Cacheable(key = "#id")
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public UserDTO queryById(Long id) {
        return userRepository.findById(id)
                .map(User::toValueObject)
                .orElse(null);
    }

    /**
     * 查询所有用户
     *
     * @return 所有用户信息
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public List<UserDTO> queryAll() {
        return userRepository.findAll()
                .stream()
                .map(User::toValueObject)
                .collect(Collectors.toList());
    }

    /**
     * 密码登录校验用户密码
     *
     * @param request 密码校验请求
     * @return 用户信息
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class, readOnly = true)
    public UserDTO verifyPassword(VerifyPasswordRequest request) {
        User user = userRepository.findByName(request.getName())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.USER_NOT_EXIST));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new Mall3Exception(ResultCodes.INVALID_PASSWORD);
        }

        return user.toValueObject();
    }

    /**
     * 校验用户Web钱包地址
     *
     * @param request 用户Web钱包地址校验请求
     * @return 用户信息
     */
    @Transactional(timeout = 30, rollbackFor = Exception.class)
    public UserDTO verifyWeb3Addr(VerifyWeb3AddressRequest request) {
        String nonce = getNonce(request.getPubAddress());
        boolean isValid = web3Operations.validate(request.getSignature(), nonce, request.getPubAddress());
        if (!isValid) {
            throw new Mall3Exception(ResultCodes.INVALID_SIGNATURE);
        }

        User user = userRepository.findByPubWeb3Addr(request.getPubAddress())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setPubWeb3Addr(request.getPubAddress());
                    userRepository.save(newUser);
                    return newUser;
                });
        clearNonce(request.getPubAddress());
        return user.toValueObject();
    }

    /**
     * 获取用户随机字符串
     *
     * @param web3Address 用户Web钱包地址
     * @return 用户随机字符串
     */
    public String getNonce(String web3Address) {
        String redisKey = "Web3Nonce_" + web3Address;
        String nonce = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(nonce)) {
            nonce = UuidGenerator.generate();
            redisTemplate.opsForValue().set(redisKey, nonce);
        }

        return nonce;
    }

    /**
     * 清除用户随机字符串
     *
     * @param web3Address 用户Web钱包地址
     */
    public void clearNonce(String web3Address) {
        String redisKey = "Web3Nonce_" + web3Address;
        redisTemplate.delete(redisKey);
    }

    /**
     * 绑定用户信息
     *
     * @param request 请求
     * @return 绑定成功后用户信息
     */
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public UserDTO bindUserInfo(BindUserInfoRequest request) {
        String password = request.getPassword();
        if (StringUtils.isNotBlank(password)) {
            if (!request.getPassword().equals(request.getRePassword())) {
                throw new Mall3Exception(ResultCodes.PASSWORD_NOT_EQUAL);
            }
            password = BCrypt.hashpw(password, BCrypt.gensalt());
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.USER_NOT_EXIST));

        if (StringUtils.isNotBlank(request.getName())) {
            User sameNameUser = userRepository.findByName(request.getName()).orElse(null);
            if (null != sameNameUser && !sameNameUser.getId().equals(user.getId())) {
                throw new Mall3Exception(ResultCodes.USER_NAME_EXIST);
            }
        }
        ObjectUtils.copy(request, user, true);
        if (StringUtils.isNotBlank(password)) {
            user.setPassword(password);
        }

        return userRepository.save(user).toValueObject();
    }

    /**
     * 绑定Web3钱包地址
     *
     * @param request 请求
     * @return 绑定成功后用户信息
     */
    @Transactional(timeout = 1000, rollbackFor = Exception.class)
    public UserDTO bindWeb3Address(BindWeb3AddressRequest request) {
        String nonce = getNonce(request.getPubAddress());
        boolean isValid = web3Operations.validate(request.getSignature(), nonce, request.getPubAddress());
        if (!isValid) {
            throw new Mall3Exception(ResultCodes.INVALID_SIGNATURE);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.USER_NOT_EXIST));
        User sameWeb3AddressUser = userRepository.findByPubWeb3Addr(request.getPubAddress()).orElse(null);
        if (null != sameWeb3AddressUser && !sameWeb3AddressUser.getId().equals(user.getId())) {
            throw new Mall3Exception(ResultCodes.WEB3_ADDRESS_EXIST);
        }

        user.setPubWeb3Addr(request.getPubAddress());
        userRepository.saveAndFlush(user);
        clearNonce(request.getPubAddress());
        return user.toValueObject();
    }
}

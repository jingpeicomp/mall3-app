package com.bik.web3.mall3.auth.login.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统登陆响应结果
 *
 * @author Mingo.Liu
 */
@ApiModel(description = "系统登陆响应结果")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse implements Serializable {
    /**
     * 登陆用户信息
     */
    @ApiModelProperty("登陆用户信息")
    private LoginUser loginUser;

    /**
     * 用户token
     */
    @ApiModelProperty("用户token")
    private String token;

    private String roleType = "admin";

    /**
     * 到期时间
     */
    @ApiModelProperty("到期时间，格式为：yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;
}

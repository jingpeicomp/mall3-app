package com.bik.web3.mall3.auth.login.dto;

import com.bik.web3.mall3.bean.user.dto.UserDTO;
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
 * 登陆用户信息
 *
 * @author Mingo.Liu
 */
@ApiModel(description = "登陆用户信息")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginUser implements Serializable {
    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 用户token
     */
    @ApiModelProperty("用户token")
    private String token;

    /**
     * 用户详情
     */
    @ApiModelProperty("用户详情")
    private UserDTO detail;

    /**
     * 登陆时间
     */
    @ApiModelProperty("登陆时间，格式为：yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime loginTime;

    /**
     * 刷新时间
     */
    @ApiModelProperty("刷新时间，格式为：yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime refreshTime;
}

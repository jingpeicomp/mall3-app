package com.bik.web3.mall3.domain.user;

import com.bik.web3.mall3.bean.user.dto.UserDTO;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户实体对象
 *
 * @author Mingo.Liu
 * @date 2022-12-07
 */
@Data
@Entity
@Table(name = "t_user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint not null comment 'ID，主键'")
    private Long id;

    /**
     * web3钱包账户地址
     */
    @Column(name = "pub_web3_addr", columnDefinition = "varchar(64) comment 'web3钱包账户地址'")
    private String pubWeb3Addr;

    /**
     * 用户名
     */
    @Column(name = "name", columnDefinition = "varchar(32) comment '账户名'")
    private String name;

    /**
     * 密码
     */
    @Column(name = "password", columnDefinition = "varchar(64) comment '密码'")
    private String password;

    /**
     * 版本号
     */
    @Version
    @Column(name = "version", columnDefinition = "bigint comment '版本号'")
    private Long version;

    public UserDTO toValueObject() {
        return ObjectUtils.copy(this, new UserDTO());
    }
}

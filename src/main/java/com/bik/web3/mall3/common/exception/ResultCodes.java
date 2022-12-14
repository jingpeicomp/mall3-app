package com.bik.web3.mall3.common.exception;

/**
 * 结果码
 * (1) 异常码以E+6位数字字符串，比如"E000001"，前二位数字为微服务，中间二位为模块，最后两位为错误编号
 * (2) 00为系统保留通用异常，01为业务模块异常
 * (3) E000000 表示成功
 *
 * @author Mingo.Liu
 */
public interface ResultCodes {
    /**
     * 操作成功
     */
    String SUCCESSFUL = "E000000";

    /**
     * 操作失败
     */
    String FAILED = "E000001";

    /**
     * 资源不存在
     */
    String DATA_NOT_EXISTS = "E000002";

    /**
     * 参数错误
     */
    String PARAMETER_ERROR = "E000003";

    /**
     * 您没有权限访问
     */
    String PERMISSION_DENIED = "E000004";

    /**
     * 非法字符
     */
    String ILLEGAL_CHARACTER = "E000005";

    /**
     * 功能不可用
     */
    String FORBIDDEN = "E000006";

    /**
     * 无法调用远程服务
     */
    String REMOTE_SERVICE_ERROR = "E000007";

    /**
     * 账号或密码错误
     */
    String INVALID_PASSWORD = "E000008";

    /**
     * TOKEN解析错误
     */
    String INVALID_TOKEN = "E000009";

    /**
     * TOKEN或者会话过期，需要重新登陆
     */
    String EXPIRED_TOKEN = "E000010";

    /**
     * 获取token失败
     */
    String TOKEN_GET_ERROR = "E000011";

    /**
     * HTTP上下文异常
     */
    String HTTP_CONTEXT_ERROR = "E000012";

    /**
     * Parameter传参，请求参数缺失异常
     */
    String MISSING_SERVLET_REQUEST_PARAMETER = "E000013";

    /**
     * 请求数据经过httpMessageConverter出错
     */
    String HTTP_MESSAGE_CONVERTER_ERROR = "E000014";

    /**
     * 不受支持的媒体类型
     */
    String HTTP_MEDIA_TYPE_NOT_SUPPORT = "E000015";

    /**
     * 不受支持的http请求方法
     */
    String HTTP_METHOD_NOT_SUPPORT = "E000016";

    /**
     * 参数校验失败
     */
    String VALIDATED_RESULT_ERROR = "E000017";

    /**
     * 微服务调用异常
     */
    String INVOKE_SERVICE_ERROR = "E000018";

    /**
     * 保存数据到数据库异常
     */
    String SAVE_DB_ERROR = "E000019";

    /**
     * 账号被锁定
     */
    String USER_LOCKED = "E000020";

    /**
     * 用户不存在
     */
    String USER_NOT_EXIST = "E000021";

    /**
     * 用户未登陆
     */
    String USER_NOT_LOGIN = "E000022";

    /**
     * 用户已被禁用
     */
    String USER_IS_DISABLE = "E000023";

    /**
     * 密码不合法，密码必须包含字母和数字，不少于8位
     */
    String PASSWORD_INVALID = "E000024";

    /**
     * 管理员角色不允许操作
     */
    String ADMIN_ROLE_FORBID_OPERATE = "E000025";

    /**
     * 原密码输入不正确
     */
    String INVALID_OLD_PASSWORD = "E000026";

    /**
     * 签名信息错误
     */
    String INVALID_SIGNATURE = "E000027";

    /**
     * 库存不足
     */
    String NOT_ENOUGH_STOCK = "E000028";

    /**
     * 请设置品牌
     */
    String NOT_BRAND = "E000029";

    /**
     * 两次密码不同
     */
    String PASSWORD_NOT_EQUAL = "E000030";
}

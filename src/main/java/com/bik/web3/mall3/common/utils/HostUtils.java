package com.bik.web3.mall3.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * 主机工具类
 *
 * @author Mingo.Liu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class HostUtils {
    private static final String LOCALHOST = "127.0.0.1";

    private static final String ANY_HOST = "0.0.0.0";

    private static final String LOCAL_IP = calculateLocalIp();

    private static final String HOSTNAME = calculateHostName();

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    public static String getLocalIp() {
        return LOCAL_IP;
    }

    public static String getHostname() {
        return HOSTNAME;
    }


    /**
     * 获取本地IP地址
     *
     * @return 本地IP地址
     */
    private static String calculateLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (null != interfaces) {
                while (interfaces.hasMoreElements()) {
                    NetworkInterface network = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        try {
                            InetAddress address = addresses.nextElement();
                            if (isValidAddress(address)) {
                                return address.getHostAddress();
                            }
                        } catch (Throwable ignore) {
                        }
                    }
                }
            }
        } catch (Throwable e) {
            return LOCALHOST;
        }
        return LOCALHOST;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (null == address || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (null != name
                && !ANY_HOST.equals(name)
                && !LOCALHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

    /**
     * 获取机器hostname
     *
     * @return hostname hostname
     */
    private static String calculateHostName() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostName();
        } catch (UnknownHostException ex) {
            log.error("Fail to get host name ", ex);
            return LOCAL_IP;
        }
    }
}

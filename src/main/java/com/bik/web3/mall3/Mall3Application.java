package com.bik.web3.mall3;

import com.bik.web3.mall3.common.utils.HostUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * Mall3应用主入口
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@Slf4j
public class Mall3Application {
    @SuppressWarnings("HttpUrlsUsage")
    public static void main(String[] args) {
        Environment env = SpringApplication.run(Mall3Application.class, args).getEnvironment();
        log.info("Bik Mall3 Application start successfully");
        String port = env.getProperty("server.port", "8080");
        String healthPort = env.getProperty("management.server.port", "9001");

        log.info("Access URLs:\n----------------------------------------------------------\n\t"
                        + "Local: \t\thttp://127.0.0.1:{}\n\t"
                        + "External: \thttp://{}:{}\n\t"
                        + "Swagger: \thttp://{}:{}/swagger-ui/index.html\n\t"
                        + "Health: \thttp://{}:{}/actuator/health\n----------------------------------------------------------",
                port,
                HostUtils.getLocalIp(),
                port,
                HostUtils.getLocalIp(),
                port,
                HostUtils.getLocalIp(),
                healthPort
        );
    }

    @Configuration
    public static class Mall3WebMvcConfigurer implements WebMvcConfigurer {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("forward:/static/index.html");
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("classpath:/web/", "classpath:/web/static/")
                    .setCacheControl(CacheControl.maxAge(14, TimeUnit.DAYS).cachePublic());
        }
    }
}

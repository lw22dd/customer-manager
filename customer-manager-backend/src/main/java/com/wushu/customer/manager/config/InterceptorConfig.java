package com.wushu.customer.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns(
                        "/user/login", "/user/register", "/error", "/user/test",
                        "/dist/**", // 排除所有/dist/下的请求（关键）
                        "/dist/js/**", "/dist/css/**", "/dist/*.svg", // 冗余但兜底
                        "/static/**", "/.well-known/**", "/", "/favicon.ico"
                );
    }


    /**
     * 用来指定静态资源不被拦截，否则继承WebMvcConfigurationSupport这种方式会导致静态资源无法直接访问
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 映射根路径下的 index.html 和 vite.svg（直接访问 /index.html 或 /vite.svg）
        registry.addResourceHandler("/index.html", "/vite.svg")
                .addResourceLocations("classpath:/static/");

        // 2. 映射 /css/** 路径到 static/css 目录（如 /css/vendor-p13HFRqH.css → 实际路径 classpath:/static/css/vendor-p13HFRqH.css）
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // 3. 映射 /js/** 路径到 static/js 目录（如 /js/index-BJdyvP3Q.js → 实际路径 classpath:/static/js/index-BJdyvP3Q.js）
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // （可选）如果项目不需要 /dist 目录，注释或删除以下冗余配置
        // registry.addResourceHandler("/dist/**")
        //         .addResourceLocations("classpath:/static/dist/");


    }


}
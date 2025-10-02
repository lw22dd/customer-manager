package com.wushu.customer.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// 注意：用 @Controller 而非 @RestController，因为需要返回页面（不是 JSON）
@Controller
public class SpaFallbackController {

    /**
     * 兜底转发：所有未匹配的 GET 请求（排除 API、静态资源）都转发到 index.html
     * 路径匹配规则：/** 表示所有路径，但通过 order 控制优先级（低于 API 和静态资源）
     */
    @GetMapping("/index")
    public String index() {
        return "forward:/index.html";
    }
    @GetMapping("/login")
    public String root() {
        return "forward:/index.html";
    }
}
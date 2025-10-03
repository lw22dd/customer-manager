package com.wushu.customer.manager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wushu.customer.manager.model.Result;
import com.wushu.customer.manager.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取请求路径
        String requestURI = request.getRequestURI();
        // 新增日志：打印所有进入拦截器的请求URI
        System.out.println("[JwtInterceptor] 拦截请求URI：" + requestURI);
        // 如果是登录、注册等公开接口，直接放行
        if (requestURI.startsWith("/user/login") ||
                requestURI.startsWith("/login") ||
            requestURI.startsWith("/user/register") ||
                requestURI.startsWith("/test") ||
                requestURI.equals("/index.html") || // 新增：放行 index 页面
                requestURI.startsWith("/index") ||
                requestURI.equals("/") ||     // 新增：放行根路径
                requestURI.startsWith("/static") || // 可选：放行静态资源
                requestURI.startsWith("/css") ||
                requestURI.startsWith("/js") ||
            requestURI.startsWith("/error")) {
            return true;
        }

        // 从请求头中获取token
        String token = request.getHeader("Authorization");

        // 如果没有token，返回401未授权
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.fail(401, "未提供有效的认证token")));
            return false;
        }

        // 提取token值（去除Bearer前缀）
        token = token.substring(7);

        // 验证token是否有效
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.fail(401, "token无效或已过期")));
            return false;
        }
        
        // token验证通过，放行
        System.out.println("[JwtInterceptor] 验证通过，放行请求URI：" + requestURI);
        return true;
    }
}
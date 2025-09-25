package com.wushu.customer.manager.model;

import lombok.Data;
import java.util.Date;

@Data
public class User  {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private Boolean enabled;
    private String userface;
    private Date regTime;
    
    // 原有字段保持不变
    private Boolean rememberMe;
    private String verifyCode;
    private String power;
    private Long expirationTime;

    // 为User类添加一个简化版的toString方法，仅包含基本字段
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
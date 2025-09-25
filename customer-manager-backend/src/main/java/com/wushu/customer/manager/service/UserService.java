package com.wushu.customer.manager.service;

import com.wushu.customer.manager.model.User;


import java.util.List;

// 继承UserDetailsService，为了在Security配置类中注入自定义的UserService
public interface UserService {

    List<User> getAllUsers();

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户对象
     */
    User login(String username, String password);

    /**
     * 根据ID查询用户
     *
     * @param id
     * @return
     */
    User getUserById(Long id);

    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    boolean addUser(User user);

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    boolean deleteUser(Long id);
}

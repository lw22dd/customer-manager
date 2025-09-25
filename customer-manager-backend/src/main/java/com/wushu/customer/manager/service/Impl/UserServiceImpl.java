package com.wushu.customer.manager.service.Impl;

import com.wushu.customer.manager.dao.UserMapper;
import com.wushu.customer.manager.model.User;
import com.wushu.customer.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    @Override
    public User login(String username, String password) {
        return userMapper.selectByUsernameAndPassword(username, password);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public boolean addUser(User user) {
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteUser(id) > 0;
    }
}
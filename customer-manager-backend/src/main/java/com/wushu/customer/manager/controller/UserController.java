package com.wushu.customer.manager.controller;

import com.wushu.customer.manager.model.PagingResult;
import com.wushu.customer.manager.model.Result;
import com.wushu.customer.manager.model.User;
import com.wushu.customer.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginUser) {
        System.out.println("loginUser: " + loginUser.toString());
        System.out.println("allUser: " + userService.getAllUsers());
        try {
            User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
            if (user != null) {
                System.out.println("登录成功");
                return Result.ok(user);
            } else {
                System.out.println("用户名或密码错误");
                return Result.fail("用户名或密码错误");

            }
        } catch (Exception e) {
            System.out.println("login error: " + e.getMessage());
            return Result.fail("登录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/test")
    public String test() {
        System.out.println("test");
        return "Spring Security";
    }
    
    // 获取所有用户
    @GetMapping("/all")
    public Result<PagingResult<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            PagingResult<User> pagingResult = new PagingResult<>(users, (long) users.size());
            return Result.ok(pagingResult);
        } catch (Exception e) {
            return Result.fail("查询失败: " + e.getMessage());
        }
    }

    // 根据ID获取用户
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return Result.ok(user);
            } else {
                return Result.fail("用户不存在");
            }
        } catch (Exception e) {
            return Result.fail("查询失败: " + e.getMessage());
        }
    }

    // 添加用户
    @PostMapping("/")
    public Result<Void> addUser(@RequestBody User user) {
        try {
            boolean result = userService.addUser(user);
            if (result) {
                return Result.ok();
            } else {
                return Result.fail("添加失败");
            }
        } catch (Exception e) {
            return Result.fail("添加失败: " + e.getMessage());
        }
    }

    // 更新用户
    @PutMapping("/")
    public Result<Void> updateUser(@RequestBody User user) {
        try {
            boolean result = userService.updateUser(user);
            if (result) {
                return Result.ok();
            } else {
                return Result.fail("更新失败");
            }
        } catch (Exception e) {
            return Result.fail("更新失败: " + e.getMessage());
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            boolean result = userService.deleteUser(id);
            if (result) {
                return Result.ok();
            } else {
                return Result.fail("删除失败");
            }
        } catch (Exception e) {
            return Result.fail("删除失败: " + e.getMessage());
        }
    }
}
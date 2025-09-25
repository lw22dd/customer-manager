package com.wushu.customer.manager.dao;

import com.wushu.customer.manager.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUserName(String username);

    /**
     * 查询所有用户
     *
     * @return
     */
    @Select("SELECT * FROM user")
    List<User> selectAllUsers();

    /**
     * 根据ID查询用户
     *
     * @param id
     * @return
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    /**
     * 插入用户
     *
     * @param user
     * @return
     */
    @Insert("INSERT INTO user(username, password, nickname, email) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    @Update("UPDATE user " +
            "SET username = #{username}, " +
            "password = #{password}, " +
            "nickname = #{nickname}, " +
            "email = #{email} " +
            "WHERE id = #{id}")
    int updateUser(User user);

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteUser(Long id);
}

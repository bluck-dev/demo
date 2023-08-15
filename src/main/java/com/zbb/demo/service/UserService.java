package com.zbb.demo.service;

import com.zbb.demo.entity.User;

import java.util.List;

public interface UserService {

    User findOne(Integer id);

    List<User> findAll();

    void insertUser(User user);
    void updateUser(User user);

    void testExcel(User user);
}

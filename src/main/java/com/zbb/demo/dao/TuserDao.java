package com.zbb.demo.dao;

import com.zbb.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TuserDao {

    User findOne(Integer id);

    List<User> findAll();

    void insertUser(User user);

    default void updateUser(User user){};


}

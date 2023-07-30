package com.softserve.itacademy.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private List<User> users;

    public UserServiceImpl() {
        users = new ArrayList<>();
    }

    @Override
    public User addUser(User user) {
        // TODO
        return null;
    }

    @Override
    public User updateUser(User user) {
        // TODO
        return null;
    }

    @Override
    public void deleteUser(User user) {
        // TODO
    }

    @Override
    public List<User> getAll() {
        // TODO
        return null;
    }

}

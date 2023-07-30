package com.softserve.itacademy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;

@Service
public class ToDoServiceImpl implements ToDoService {

    private UserService userService;

    @Autowired
    public ToDoServiceImpl(UserService userService) {
        this.userService = userService;
    }

    public ToDo addTodo(ToDo todo, User user) {
        // TODO
        return null;
    }

    public ToDo updateTodo(ToDo todo) {
        // TODO
        return null;
    }

    public void deleteTodo(ToDo todo) {
        // TODO
    }

    public List<ToDo> getAll() {
        // TODO
        return null;
    }

    public List<ToDo> getByUser(User user) {
        // TODO
        return null;
    }

    public ToDo getByUserTitle(User user, String title) {
        // TODO
        return null;
    }

}

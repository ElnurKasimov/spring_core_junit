package com.softserve.itacademy.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.softserve.itacademy.exceptions.ToDoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;

@Service
public class ToDoServiceImpl implements ToDoService {

    private final UserService userService;
    private final List<ToDo> toDos = new ArrayList<>();

    @Autowired
    public ToDoServiceImpl(UserService userService) {
        this.userService = userService;
    }

    public ToDo addTodo(ToDo todo, User user) {
        List<User> users = userService.getAll();
        User searchedUser = users.stream()
                .filter(user1 -> user1.equals(user))
                .findFirst()
                .orElse(null);
        if (searchedUser != null) {
            todo.setOwner(searchedUser);
            toDos.add(todo);
        } else throw new ToDoNotFoundException("ToDo with owner " + user + " has not be found!");
        return todo;
    }

    public ToDo updateTodo(ToDo todo) {
        String title = todo.getTitle();
        User user = todo.getOwner();
        ToDo toDoByUserTitle = getByUserTitle(user, title);
        toDoByUserTitle.setTasks(todo.getTasks());
        return toDoByUserTitle;
    }

    public void deleteTodo(ToDo todo) {
        if (todo != null) {
            toDos.remove(todo);
        } else throw new ToDoNotFoundException("ToDo is null!");
    }

    public List<ToDo> getAll() {
        return toDos;
    }

    public List<ToDo> getByUser(User user) {
        return toDos.stream()
                .filter(toDo -> toDo.getOwner().equals(user))
                .collect(Collectors.toList());
    }

    public ToDo getByUserTitle(User user, String title) {
        ToDo toDoByUserAndTitle = toDos.stream()
                .filter(toDo -> toDo.getOwner().equals(user) && toDo.getTitle().equals(title))
                .distinct()
                .findFirst()
                .orElse(null);
        if (toDoByUserAndTitle != null) {
            return toDoByUserAndTitle;
        } else throw new ToDoNotFoundException("ToDo with owner " + user + " and title " + title +
                " has not be found!");
    }

}

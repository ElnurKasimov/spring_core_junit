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

        if (user == null) {
            throw new ToDoNotFoundException("User can not be null!");
        }

        User searchedUser = users.stream()
                .filter(user1 -> user1.equals(user))
                .findFirst()
                .orElse(null);

        List<ToDo> toDoList = getAll();

        String title = todo.getTitle();

        ToDo toDoInList = toDoList.stream()
                .filter(toDoFromList -> toDoFromList.getTitle().equals(title)
                        && toDoFromList.getOwner().equals(user))
                .findFirst()
                .orElse(null);
        if (toDoInList != null) {
            throw new ToDoNotFoundException("This ToDo is already exist!");
        }

        if (searchedUser == null) {
            throw new ToDoNotFoundException("User has not been found!");
        }
        todo.setOwner(searchedUser);
        toDos.add(todo);

        return todo;
    }

    public ToDo updateTodo(ToDo todo) {
        if (todo == null) {
            throw new ToDoNotFoundException("ToDo is null!");
        }
        String title = todo.getTitle();
        User user = todo.getOwner();
        ToDo toDoByUserTitle = getByUserTitle(user, title);

        if (!toDos.contains(toDoByUserTitle)) {
            throw new ToDoNotFoundException("This ToDo has not been found!");
        }
        if (todo.getTasks().equals(toDoByUserTitle.getTasks())){
            throw new ToDoNotFoundException("This ToDo has the same Tasks!");
        }
        toDoByUserTitle.setTasks(todo.getTasks());
        return toDoByUserTitle;
    }


    public void deleteTodo(ToDo todo) {
        if (todo == null) {
            throw new ToDoNotFoundException("ToDo is null!");
        }
        if (!toDos.contains(todo)) {
            throw new ToDoNotFoundException("ToDo List doesn't contain this ToDo!");
        }
        toDos.remove(todo);
    }


    public List<ToDo> getAll() {
        return toDos;
    }

    public List<ToDo> getByUser(User user) {
        if (user == null) {
            throw new ToDoNotFoundException("User is null!");
        }
        List<ToDo> list = toDos.stream()
                .filter(toDo -> toDo.getOwner().equals(user))
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            throw new ToDoNotFoundException("ToDos with user " + user + " have not been found!");
        }
        return list;
    }


    public ToDo getByUserTitle(User user, String title) {
        if (user == null) {
            throw new ToDoNotFoundException("User is null!");
        }
        if (title.isEmpty()) {
            throw new ToDoNotFoundException("Title is empty!");
        }
        ToDo toDoByUserAndTitle = toDos.stream()
                .filter(toDo -> toDo.getOwner().equals(user) && toDo.getTitle().equals(title))
                .findFirst()
                .orElse(null);
        if (toDoByUserAndTitle == null) {
            throw new ToDoNotFoundException("ToDo with owner " + user + " and title " + title +
                    " has not be found!");
        }
        return toDoByUserAndTitle;
    }

}

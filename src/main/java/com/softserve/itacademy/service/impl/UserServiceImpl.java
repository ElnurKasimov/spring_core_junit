package com.softserve.itacademy.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.softserve.itacademy.exceptions.UserNotFoundException;
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
        if (user == null){
            throw new UserNotFoundException("User is null");
        }
        if (users.contains(user)){
            throw new UserNotFoundException("User is already there");
        }
        users.add(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        List<String> values = null;
        try {
            values = List.of(user.getFirstName(), user.getLastName());
        }
        catch (Exception e){
            throw new UserNotFoundException("User is null");
        }
        for (int i = 0; i < users.size(); i++) {
            List<String> tempValues = List.of(user.getFirstName(), user.getLastName());
            if (values.equals(tempValues)) {
                users.get(i).setMyTodos(user.getMyTodos());
                users.set(i, user);
                return user;
            }
        }
        throw new UserNotFoundException("User wasn't found");
    }



    @Override
    public void deleteUser(User user) {
        if (user == null){
            throw new UserNotFoundException("User is null");
        }
        if (users.stream().anyMatch(x->x.getEmail().equals(user.getEmail()))){
            users.remove(user);
        }
        else{
            throw new UserNotFoundException("User wasn't found");
        }
    }
    @Override
    public List<User> getAll() {
        return users;
    }

}

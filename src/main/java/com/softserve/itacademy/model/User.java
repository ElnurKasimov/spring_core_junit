package com.softserve.itacademy.model;

import java.util.List;
import java.util.Objects;

public class User {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private List<ToDo> myTodos;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ToDo> getMyTodos() {
        return myTodos;
    }

    public void setMyTodos(List<ToDo> myTodos) {
        this.myTodos = myTodos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(myTodos, user.myTodos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, password, myTodos);
    }

    // Constructor(s), getters, setters, hashCode, equals, etc.

}

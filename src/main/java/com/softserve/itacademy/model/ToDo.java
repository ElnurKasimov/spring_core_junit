package com.softserve.itacademy.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ToDo {

    private String title;

    private final LocalDateTime createdAt;

    private User owner;

    private List<Task> tasks;

    public ToDo(String title, User owner) {
        this.title = title;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();

    }

    public ToDo(String title, User owner, List<Task> tasks) {
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.owner = owner;
        this.tasks = tasks;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDo toDo = (ToDo) o;
        return Objects.equals(title, toDo.title) && Objects.equals(createdAt, toDo.createdAt) && Objects.equals(owner, toDo.owner) && Objects.equals(tasks, toDo.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, createdAt, owner, tasks);
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", owner=" + owner +
                ", tasks=" + tasks +
                '}';
    }
}

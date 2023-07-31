package com.softserve.itacademy.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.softserve.itacademy.exceptions.TaskNotFoundException;
import com.softserve.itacademy.exceptions.ToDoIllegalArgumentException;
import com.softserve.itacademy.exceptions.ToDoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;

@Service
public class TaskServiceImpl implements TaskService {

    private ToDoService toDoService;

    @Autowired
    public TaskServiceImpl(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    public Task addTask(Task task, ToDo todo) {
        if (todo == null) throw new IllegalArgumentException("toDo must not be null");
        if (task == null) throw new IllegalArgumentException("task must not be null");
        if(toDoService.getAll().stream().noneMatch(td -> td.equals(todo)))
            throw new ToDoNotFoundException("There is no such toDo: " + todo);
        List<Task> taskListToAdd = todo.getTasks();
        if( taskListToAdd.stream().anyMatch(t -> t.equals(task)) ) throw new ToDoIllegalArgumentException(
                "ToDo " + todo + " contains task " + task + " already.  There should only be one task.");
        taskListToAdd.add(task);
        todo.setTasks(taskListToAdd);
        return task;
    }

    public Task updateTask(Task task) {
        if (task == null) throw new IllegalArgumentException("task must not be null");
        if(getAll().stream()
                .map(Task::getName)
                .noneMatch(name -> name.equals(task.getName())))
            throw new TaskNotFoundException("There is no such task: " + task);
        // ---------------------------------------------------------
        Predicate<ToDo> ifToDoContainsMoreThanOneEqualTask =
                toDo -> toDo.getTasks().stream().filter(t -> t.equals(task)).skip(1).findFirst().orElse(null) != null;
        ToDo containsMoreThanOneEqualTask =  toDoService.getAll().stream()
                .filter( ifToDoContainsMoreThanOneEqualTask)
                .findFirst().orElse(null);
// --------------------------------------------------------------------

        Predicate<ToDo> ifToDoContainsTask =
                toDo -> toDo.getTasks().stream().filter(t -> t.equals(task)).findFirst().orElse(null) != null;
        ToDo containsTask =  toDoService.getAll().stream()
                .filter( ifToDoContainsTask)
                .findFirst().orElse(null);
        int taskIndex = containsTask.getTasks().indexOf(task);
        containsTask.getTasks().set(taskIndex, task);
        return task;
    }

    public void deleteTask(Task task) {
        if (task == null) throw new IllegalArgumentException("task must not be null");
        if(getAll().stream()
                .map(Task::getName)
                .noneMatch(name -> name.equals(task.getName())))
            throw new TaskNotFoundException("There is no such task: " + task);
        Predicate<ToDo> ifToDoContainsTask =  toDo -> toDo.getTasks().stream().filter(t -> t.equals(task)).findFirst().orElse(null) != null;

        // to simplify predicate's code use method  getByToDo.  obtain list and list.contains(element)


        ToDo containsTask =  toDoService.getAll().stream()
                .filter( ifToDoContainsTask)
                .findFirst().orElse(null);

    }

    public List<Task> getAll() {
        return toDoService.getAll().stream()
                .flatMap(toDo -> toDo.getTasks().stream())
                .collect(Collectors.toList());
    }

    public List<Task> getByToDo(ToDo todo) {
        //проверить вход на нал
        return todo.getTasks();
    }

    public Task getByToDoName(ToDo todo, String name) {
       if (todo == null) throw new IllegalArgumentException("toDo must not be null");
       if (name == null || name.isEmpty()) throw new IllegalArgumentException("name must not be null or empty");
       // pay attention to the test
       return todo.getTasks().stream()
               .filter(task -> task.getName().equals(name))
               .findFirst().orElse(null);
    }

    public Task getByUserName(User user, String name) {
        // TODO
        return null;
    }

}

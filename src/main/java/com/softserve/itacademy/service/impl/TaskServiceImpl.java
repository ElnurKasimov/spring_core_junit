package com.softserve.itacademy.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.softserve.itacademy.exceptions.*;

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
        if (toDoService.getAll().stream().noneMatch(td -> td.equals(todo)))
            throw new ToDoValidationException("There is no such toDo: " + todo);
        List<Task> taskListToAdd = new ArrayList<>(todo.getTasks());
        if (taskListToAdd.contains(task)) throw new DublicateTaskException(
                "ToDo " + todo + " contains task " + task + " already.  There should only be one task.");
        if (!taskListToAdd.add(task)) throw new AddTaskException("Unsuccessful deleting");
        return task;
    }

    public Task updateTask(Task task) {
        if (task == null) throw new IllegalArgumentException("task must not be null");
        Predicate<ToDo> ifToDoContainsTask = toDo -> getByToDo(toDo).contains(task);
        ToDo containsTask = toDoService.getAll().stream()
                .filter(ifToDoContainsTask)
                .findFirst().orElseThrow(() -> new TaskNotFoundException("There is no such task: " + task));
        List<Task> duplicatesTask = getDuplicatesTaskFromToDo(toDoService.getAll());
        if (!duplicatesTask.isEmpty())
            throw new DublicateTaskException("There should only be one task. But such tasks are duplicated: \n" + duplicatesTask);
        List<Task> listToUpdate = new ArrayList<>(containsTask.getTasks());
        int taskIndex = listToUpdate.indexOf(task);
        listToUpdate.set(taskIndex, task);
        return task;
    }

    public void deleteTask(Task task) {
        if (task == null) throw new IllegalArgumentException("task must not be null");
        Predicate<ToDo> ifToDoContainsTask = toDo -> getByToDo(toDo).contains(task);
        ToDo containsTask = toDoService.getAll().stream()
                .filter(ifToDoContainsTask)
                .findFirst().orElseThrow(() -> new TaskNotFoundException("There is no such task: " + task));
        List<Task> duplicatesTask = getDuplicatesTaskFromToDo(toDoService.getAll());
        if (!duplicatesTask.isEmpty())
            throw new DublicateTaskException("There should only be one task. But such tasks are duplicated: \n" + duplicatesTask);
        List<Task> listToDelete = new ArrayList<>(containsTask.getTasks());
        if (! listToDelete.remove(task)) throw new DeleteTaskException("Unsuccessful deleting");
        containsTask.setTasks(listToDelete);
    }

    public List<Task> getDuplicatesTaskFromToDo(List<ToDo> toDos) {
        Map<Task, Long> duplicatesMap = toDos.stream()
                .flatMap(t -> t.getTasks().stream())
                .collect(Collectors.groupingBy(task -> task, Collectors.counting()));
        return duplicatesMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Task> getAll() {
        return toDoService.getAll().stream()
                .flatMap(toDo -> toDo.getTasks().stream())
                .collect(Collectors.toList());
    }

    public List<Task> getByToDo(ToDo todo) {
        if (todo == null) throw new IllegalArgumentException("toDo must not be null");
        if (toDoService.getAll().stream().noneMatch(td -> td.equals(todo)))
            throw new ToDoValidationException("There is no such toDo: " + todo);
        return todo.getTasks();
    }

    public Task getByToDoName(ToDo todo, String name) {
        if (todo == null) throw new IllegalArgumentException("toDo must not be null");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("name must not be null or empty");
        if (toDoService.getAll().stream().noneMatch(td -> td.equals(todo)))
            throw new ToDoValidationException("There is no such toDo: " + todo);
        return todo.getTasks().stream()
                .filter(task -> task.getName().equals(name))
                .findFirst().orElse(null);
    }

    public Task getByUserName(User user, String name) {
        if (user == null) throw new IllegalArgumentException("user must not be null");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("name must not be null or empty");
        Task withSuchName = getAll().stream()
                .filter(task -> task.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException("There is no task with name " + name));
        List<Task> tasks = new ArrayList<>();
        for (ToDo todo : toDoService.getAll()) {
            try {
                if (toDoService.getByUserTitle(user, todo.getTitle()).getTasks().contains(withSuchName));
            } catch (Exception e) {
                throw new UserNotFoundException("There is no user : " + user) ;
            }
            tasks.add(withSuchName);
        }
        if (tasks.isEmpty()) throw new TaskNotFoundException("No user has task with name" + name);
        if (tasks.size() > 1) throw new DublicateTaskException("More than one user has task with name " + name);
        return withSuchName;
    }

}

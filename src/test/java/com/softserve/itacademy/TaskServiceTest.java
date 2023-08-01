package com.softserve.itacademy;

import com.softserve.itacademy.exceptions.DublicateTaskException;
import com.softserve.itacademy.exceptions.TaskNotFoundException;
import com.softserve.itacademy.exceptions.ToDoNotFoundException;
import com.softserve.itacademy.exceptions.UserNotFoundException;
import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.junit.jupiter.params.ParameterizedTest;
import com.softserve.itacademy.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class TaskServiceTest {
    private static UserService userService;
    private static ToDoService toDoService;
    private static TaskService taskService;

    private static User teamLead;
    private static User developer;
    private static ToDo checkCode;
    private static ToDo createCode;

    private static Task makeReview;
    private static Task makeRefactoring;
    private static Task writeCode;
    private static Task writeTests;

     @BeforeAll
    public static void setupBeforeClass() throws Exception {
        AnnotationConfigApplicationContext annotationConfigContext = new AnnotationConfigApplicationContext(Config.class);
        userService = annotationConfigContext.getBean(UserService.class);
        toDoService = annotationConfigContext.getBean(ToDoService.class);
        taskService = annotationConfigContext.getBean(TaskService.class);
        annotationConfigContext.close();
    }

    @BeforeAll
    public static void initAllNecessaryModels() {
        teamLead = new User("Ilon", "Mask", "wer@com.ua", "qwerty");
        developer = new User("Petro", "Stepanov", "petro@gmail.com", "asdfg");
        userService.addUser(teamLead);
        userService.addUser(developer);

        makeReview = new Task("makeReview", Priority.HIGH);
        makeRefactoring = new Task("makeRefactoring", Priority.LOW);
        checkCode = new ToDo("CheckCode", teamLead,
                List.of(makeReview, makeRefactoring));
        writeCode = new Task("writeCode", Priority.HIGH);
        writeTests = new Task("writeTests", Priority.HIGH);
        createCode = new ToDo("createCode",  developer,
                List.of(writeCode, writeTests));
        toDoService.addTodo(checkCode, teamLead);
        toDoService.addTodo(createCode,developer);
    }

    @ParameterizedTest (name = "#{index} - Test  that checks if  is it possible to pass in parameter addTask   task = {0} and todo = {1}")
    @MethodSource("predefinedAddTaskArgumentsForCheckingNull")
    public void testThatAddTaskArgumentsNotContainsNull(Task task, ToDo todo) {
        assertThrows(IllegalArgumentException.class,() -> taskService.addTask(task, todo));
    }

    private static Stream<Arguments> predefinedAddTaskArgumentsForCheckingNull() {
        return
                Stream.of(
                        Arguments.arguments(null, checkCode),
                        Arguments.arguments(makeReview, null),
                        Arguments.arguments(null, null)
                );
    }

    @Test
    @DisplayName("Test that checks if  ToDo in parameter addTask exist")
    public void testThatToDoNotExistInArgumentsAddTask() {
        //given
        ToDo forTestOfPresenceToDo = new ToDo("test", teamLead);
         // when then
        assertThrows(ToDoNotFoundException.class,() -> taskService.addTask(makeRefactoring, forTestOfPresenceToDo));
    }

    @Test
    @DisplayName("Test that checks if  task to add with addTask() exist already")
    public void testThatThereIsNoDuplicatedTasks() {
        assertThrows(DublicateTaskException.class,() -> taskService.addTask(writeCode, createCode));
    }

    @Test
    @DisplayName("Test that checks if  addTask()  works correctly and no exception will be thrown")
    public void testThatAddTaskArgumentsNotNullWorkCorrectly() {
        Task actual = taskService.addTask(new Task("ttt", Priority.HIGH), checkCode);
        Task expected = new Task("ttt", Priority.HIGH);
        Assertions.assertEquals(expected, actual, "addTask() doesn't work properly.");
    }

    @Test
    @DisplayName("Test that parameter updateTask()  shouldn't be null")
    public void testThatUpdateTaskArgumentNotContainsNull() {
        assertThrows(IllegalArgumentException.class,() -> taskService.updateTask(null));
    }

    @Test
    @DisplayName("Test that task in parameter updateTask()  exists already")

    public void testThatUpdateTaskArgumentExistAlready() {
        //given
        Task someTask = new Task("someTask", Priority.LOW);
        // when then
        assertThrows(TaskNotFoundException.class,() -> taskService.updateTask(someTask));
    }

    @Test
    @DisplayName("Test that updateTask() works when there is no duplicated tasks")
    public void testThatUpdateTaskDoesNotWorkWithDuplicatedTasks() {
        //given
        User tester = new User("Mykola", "Stasiv", "mykola@gmail.com", "kjhvfg");
        userService.addUser(tester);
        Task checkTests = new Task("writeTests", Priority.HIGH);
        ToDo testing = new ToDo("testing",  tester,  List.of(writeTests));
        toDoService.addTodo(testing, tester);
        // when then
        assertThrows(DublicateTaskException.class,() -> taskService.updateTask(makeReview));
        toDoService.deleteTodo(testing);
        userService.deleteUser(tester);
    }

    @Test
    @DisplayName("Test that updateTask() works correctly")
    public void testThatUpdateTaskWorksCorrectly() {
        //given
        makeRefactoring.setPriority(Priority.HIGH);
        // when
        Task actual = taskService.updateTask(makeRefactoring);
        // then
        Task expected = new Task("makeRefactoring", Priority.HIGH);
        assertEquals(expected, actual);
        makeRefactoring.setPriority(Priority.LOW);
    }

    @Test
    @DisplayName("Test that parameter deleteTask()  shouldn't be null")
    public void testThatDeleteTaskArgumentNotContainsNull() {
        assertThrows(IllegalArgumentException.class,() -> taskService.deleteTask(null));
    }

    @Test
    @DisplayName("Test that task in parameter updateTask()  is absent")

    public void testThatDeleteTaskArgumentExistAlready() {
        //given
        Task absentTask = new Task("absentTask", Priority.LOW);
        // when then
        assertThrows(TaskNotFoundException.class,() -> taskService.deleteTask(absentTask));
    }

    @Test
    @DisplayName("Test that updateTask() works when there is no duplicated tasks")
    public void testThatDeleteTaskDoesNotWorkWithDuplicatedTasks() {
        //given
        User tester = new User("Mykola", "Stasiv", "mykola@gmail.com", "kjhvfg");
        userService.addUser(tester);
        Task checkTests = new Task("writeTests", Priority.HIGH);
        ToDo testing = new ToDo("testing",  tester,  List.of(writeTests));
        toDoService.addTodo(testing, tester);
        // when then
        assertThrows(DublicateTaskException.class,() -> taskService.deleteTask(makeReview));
        toDoService.deleteTodo(testing);
        userService.deleteUser(tester);
    }

    @Test
    @DisplayName("Test that deleteTask() works correctly")
    public void testThatDeleteTaskWorksCorrectly() {
        //given
        // when
       taskService.deleteTask(makeRefactoring);
        // then
        List<Task> expected = List.of(makeReview);
        assertEquals(expected, checkCode.getTasks());
        taskService.addTask(makeRefactoring, checkCode);
    }

    @Test
    @DisplayName("Test that getAll() works correctly")
    public void testThatGetAllWorksCorrectly() {
        //given
        // when
        taskService.getAll();
        // then
        List<Task> expected = List.of(makeReview, makeRefactoring, writeCode, writeTests);
        assertEquals(expected, taskService.getAll());
    }

    @Test
    @DisplayName("Test that parameter getByToDo()  shouldn't be null")
    public void testThatGetByToDoArgumentNotContainsNull() {
        assertThrows(IllegalArgumentException.class,() -> taskService.getByToDo(null));
    }

    @Test
    @DisplayName("Test that todo in parameter  getByToDo()  exists already")

    public void testThatGetByToDoArgumentExistAlready() {
        //given
        ToDo someToDo = new ToDo("someToDo", teamLead, new ArrayList<>() );
        // when then
        assertThrows(ToDoNotFoundException.class,() -> taskService.getByToDo(someToDo));
    }

    @Test
    @DisplayName("Test that getByToDo() works correctly")
    public void testThatGetByToDoWorksCorrectly() {
        //given
        // when
        // then
        List<Task> expected = List.of(makeReview, makeRefactoring);
        assertEquals(expected, taskService.getByToDo(checkCode));
    }

    @ParameterizedTest (name = "#{index} - Test  that checks if  is it possible to pass in parameter getByToDoName  todo = {0} and name = {1}")
    @MethodSource("predefinedGetByToDoNameArgumentsForCheckingNull")
    public void testThatGetByToDoNameArgumentsNotContainsNull(ToDo todo, String name) {
        assertThrows(IllegalArgumentException.class,() -> taskService.getByToDoName(todo, name));
    }

    private static Stream<Arguments> predefinedGetByToDoNameArgumentsForCheckingNull() {
        return
                Stream.of(
                        Arguments.arguments(null, "writeCode"),
                        Arguments.arguments(checkCode, null),
                        Arguments.arguments(checkCode, ""),
                        Arguments.arguments(null, null)
                );
    }

    @Test
    @DisplayName("Test that todo in parameter  getByToDoName()  exists already")

    public void testThatToDoArgumentExistAlready() {
        //given
        ToDo someToDo = new ToDo("someToDo", teamLead, new ArrayList<>() );
        // when then
        assertThrows(ToDoNotFoundException.class,() -> taskService.getByToDoName(someToDo, "writeCode"));
    }

    @Test
    @DisplayName("Test that name in parameter  getByToDoName()  exists already")

    public void testThatNameArgumentExistAlready() {
        //given
        // when then
        assertThrows(TaskNotFoundException.class,() -> taskService.getByToDoName(checkCode, "writeCode"));
    }

    @Test
    @DisplayName("Test that getByToDoName() works correctly")
    public void testThatGetByToDoNameWorksCorrectly() {
        //given
        // when
        // then
        Task expected = writeCode;
        assertEquals(expected, taskService.getByToDoName(createCode, "writeCode"));
    }

    @ParameterizedTest (name = "#{index} - Test  that checks if  is it possible to pass in parameter getByUserName  user = {0} and name = {1}")
    @MethodSource("predefinedGetByUserNameArgumentsForCheckingNull")
    public void testThatGetByUserNameArgumentsNotContainsNull(User user, String name) {
        assertThrows(IllegalArgumentException.class,() -> taskService.getByUserName(user, name));
    }

    private static Stream<Arguments> predefinedGetByUserNameArgumentsForCheckingNull() {
        return
                Stream.of(
                        Arguments.arguments(null, "writeCode"),
                        Arguments.arguments(developer, null),
                        Arguments.arguments(developer, ""),
                        Arguments.arguments(null, null)
                );
    }

    @Test
    @DisplayName("Test that user in parameter  getByUserName()  exists already")

    public void testThatUserArgumentExistAlready() {
        //given
        User tester = new User("Mykola", "Stasiv", "mykola@gmail.com", "kjhvfg");
        // when then
        assertThrows(UserNotFoundException.class,() -> taskService.getByUserName(tester, "writeTests"));
    }

    @Test
    @DisplayName("Test that name in parameter  getByUserName()  exists already")

    public void testThatNameInArgumentExistAlready() {
        //given
        // when then
        assertThrows(TaskNotFoundException.class,() -> taskService.getByUserName(developer, "coverCodeByTests"));
    }

    @Test
    @DisplayName("Test that getByUserName() works correctly")
    public void testThatGetByUserNameWorksCorrectly() {
        //given
        // when
        // then
        Task expected = writeCode;
        assertEquals(expected, taskService.getByUserName(developer, "writeCode"));
    }

}

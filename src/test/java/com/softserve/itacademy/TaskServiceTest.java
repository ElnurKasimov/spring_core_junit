package com.softserve.itacademy;

import com.softserve.itacademy.exceptions.DublicateTaskException;
import com.softserve.itacademy.exceptions.ToDoNotFoundException;
import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import com.softserve.itacademy.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.junit.jupiter.params.ParameterizedTest;
import com.softserve.itacademy.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(JUnitPlatform.class)
public class TaskServiceTest {
    private static UserService userService;
    private static ToDoService toDoService;
    private static TaskService taskService;

    private static User manager;
    private static User developer;
    private static ToDo checkTests;
    private static ToDo createTests;

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
    public static void initUserAndTodo() {
        manager = new User("Ilon", "Mask", "wer@com.ua", "qwerty");
        developer = new User("Petro", "Stepanov", "petro@gmail.com", "asdfg");
        userService.addUser(manager);
        userService.addUser(developer);

        makeReview = new Task("makeReview", Priority.HIGH);
        makeRefactoring = new Task("makeRefactor", Priority.LOW);
        checkTests = new ToDo("CheckTests",  manager,
                List.of(makeReview, makeRefactoring));
        writeCode = new Task("writeCode", Priority.HIGH);
        writeTests = new Task("writeTests", Priority.HIGH);
        createTests = new ToDo("createTests",  developer,
                List.of(writeCode, writeTests));
        toDoService.addTodo(checkTests,manager);
        toDoService.addTodo(createTests,developer);
    }

    @ParameterizedTest (name = "#{index} - Test with task = {0} and todo = {1}")
    @MethodSource("predefinedAddTaskArgumentsForCheckingNull")
    public void testThatAddTaskArgumentsNotContainsNull(Task task, ToDo todo) {
        //given
        // when then
        assertThrows(IllegalArgumentException.class,() -> taskService.addTask(task, todo));
    }

    private static Stream<Arguments> predefinedAddTaskArgumentsForCheckingNull() {
        return
                Stream.of(
                        Arguments.arguments(null, checkTests),
                        Arguments.arguments(makeReview, null),
                        Arguments.arguments(null, null)
                );
    }

    @Test
    public void testThatToDoNotExistInArgumentsAddTask() {
        //given
        ToDo forTestOfPresenceToDo = new ToDo("test",manager);
         // when then
        assertThrows(ToDoNotFoundException.class,() -> taskService.addTask(makeRefactoring, forTestOfPresenceToDo));
    }

    @Test
    public void testThatThereIsNoDuplicatedTasks() {
        assertThrows(DublicateTaskException.class,() -> taskService.addTask(writeCode, createTests));
    }

    @Test
    public void testThatAddTaskArgumentsNotNullWorkCorrectly() {
        Task actual = taskService.addTask(new Task("ttt", Priority.HIGH), checkTests);

        Task task = new Task("ttt", Priority.HIGH);

        Assertions.assertEquals(task, actual);

    }








    // TODO, other tests
}

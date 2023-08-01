package com.softserve.itacademy;

import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.junit.jupiter.params.ParameterizedTest;
import com.softserve.itacademy.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(JUnitPlatform.class)
public class TaskServiceTest {
    private static TaskService taskService;
    private static ToDoService toDoService;
    private static User manager;
    private static ToDo checkTests;
    private static Task makeReview;
    private static Task makeRefactor;


    @BeforeAll
    public static void setupBeforeClass() throws Exception {
        AnnotationConfigApplicationContext annotationConfigContext = new AnnotationConfigApplicationContext(Config.class);
        taskService = annotationConfigContext.getBean(TaskService.class);
        toDoService = annotationConfigContext.getBean(ToDoService.class);
        annotationConfigContext.close();
    }

    @BeforeAll
    public static void initUserAndTodo() {
        manager = new User("Ilon", "Mask", "wer@com.ua", "qwerty");
        makeReview = new Task("makeReview", Priority.HIGH);
        makeRefactor = new Task("makeRefactor", Priority.LOW);
        checkTests = new ToDo("CheckTests", LocalDateTime.now(), manager,
                List.of(makeReview,makeRefactor));
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








    // TODO, other tests
}

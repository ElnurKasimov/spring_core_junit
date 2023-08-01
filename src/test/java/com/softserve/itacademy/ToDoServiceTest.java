package com.softserve.itacademy;

import com.softserve.itacademy.exceptions.ToDoNotFoundException;
import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.impl.ToDoServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(JUnitPlatform.class)
public class ToDoServiceTest {
    private static UserService userService;
    private static ToDoServiceImpl toDoService;


    @BeforeAll
    public static void setupBeforeClass() throws Exception {
        AnnotationConfigApplicationContext annotationConfigContext = new AnnotationConfigApplicationContext(Config.class);
        userService = annotationConfigContext.getBean(UserService.class);
        annotationConfigContext.close();

        toDoService = new ToDoServiceImpl(userService);


    }

    @Test
    @DisplayName("Test adding new ToDo with null")
    public void checkAddToDoWithNullUser() {
        User oldUser = new User();
        oldUser.setEmail("email@gmail.com");

        ToDo toDo = new ToDo("Test", oldUser);

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.addTodo(toDo, null));

    }

    @Test
    @DisplayName("Test adding existed ToDo")
    public void checkAddExistedToDo() {
        User user = new User();
        user.setEmail("email@gmail.com");

        ToDo toDo = new ToDo("Test", user);
        toDoService.addTodo(toDo, user);

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.addTodo(toDo, user));
    }

    @Test
    @DisplayName("Test adding ToDo with another User")
    public void checkAddToDoWithAnotherUser() {
        User oldUser = new User();
        oldUser.setEmail("email@gmail.com");

        User newUser = new User();
        newUser.setEmail("new@email.com");

        ToDo toDo = new ToDo("Test", oldUser);

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.addTodo(toDo, newUser));
    }

    @Test
    @DisplayName("Test adding new ToDo with new User")
    public void checkAddValidToDo() {
        User oldUser = new User();
        oldUser.setEmail("email@gmail.com");

        User newUser = new User();
        newUser.setEmail("new@email.com");

        userService.addUser(newUser);

        ToDo toDo = new ToDo("Test", oldUser);

        ToDo actual = toDoService.addTodo(toDo, newUser);
        ToDo expected = toDoService.getAll().stream()
                .filter(toDoFromList -> toDoFromList.equals(toDo))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected, actual);

        toDoService.deleteTodo(toDo);
    }

    @Test
    @DisplayName("Test update ToDo with null")
    public void checkUpdateToDoWithNull() {
        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.updateTodo(null));
    }

    @Test
    @DisplayName("Test update non existed ToDo")
    public void checkUpdateNonExistedToDo() {
        User user = new User();
        user.setEmail("new@email.com");
        ToDo toDo = new ToDo("Test", user);

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.updateTodo(toDo));
    }

    @Test
    @DisplayName("Test update valid ToDo")
    public void checkUpdateValidToDo() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);

        ToDo toDoNew = new ToDo("Test", user);
        Task taskNew = new Task("Task #1", Priority.LOW);
        toDoNew.setTasks(List.of(taskNew));

        ToDo toDoOld = new ToDo("Test", user);
        Task taskOld = new Task("Task #3", Priority.MEDIUM);
        toDoOld.setTasks(List.of(taskOld));

        toDoService.addTodo(toDoOld, user);


        ToDo actual = toDoService.updateTodo(toDoNew);

        Assertions.assertEquals(List.of(taskNew), actual.getTasks());

        toDoService.deleteTodo(toDoOld);

    }

    @Test
    @DisplayName("Test delete ToDo with null")
    public void checkDeleteToDoWithNull() {
        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.deleteTodo(null));
    }

    @Test
    @DisplayName("Test delete non existed ToDo")
    public void checkDeleteNonExistedToDo() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);

        ToDo toDo = new ToDo("Test", user);

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.deleteTodo(toDo));
    }

    @Test
    @DisplayName("Test delete valid ToDo")
    public void checkDeleteValidToDo() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);

        ToDo toDo = new ToDo("Test", user);
        toDoService.addTodo(toDo, user);

        toDoService.deleteTodo(toDo);

        Assertions.assertFalse(toDoService.getAll().contains(toDo));

    }

    @Test
    @DisplayName("Test get All ToDo")
    public void checkGetAllToDo() {
        User user1 = new User();
        user1.setEmail("emailuser1@gmail.com");

        User user2 = new User();
        user2.setEmail("emailuser2@gmail.com");

        userService.addUser(user1);
        userService.addUser(user2);

        ToDo toDo1 = new ToDo("Test1", user1);
        ToDo toDo2 = new ToDo("Test2", user2);

        toDoService.addTodo(toDo1, user1);
        toDoService.addTodo(toDo2, user2);

        List<ToDo> list = List.of(toDo1, toDo2);

        Assertions.assertEquals(list, toDoService.getAll());

        toDoService.deleteTodo(toDo1);
        toDoService.deleteTodo(toDo2);
    }

    @Test
    @DisplayName("Test get ToDo by null User ")
    public void checkGetToDoByNullUser() {
        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.getByUser(null));
    }

    @Test
    @DisplayName("Test get ToDo by non existed User ")
    public void checkGetToDoByNonExistedUser() {
        User user = new User();
        user.setEmail("emailuser@gmail.com");
        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.getByUser(user));
    }

    @Test
    @DisplayName("Test get ToDo by  User ")
    public void checkGetToDoByUser() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);

        ToDo toDo = new ToDo("Test", user);
        toDoService.addTodo(toDo, user);

        List<ToDo> actual = toDoService.getByUser(user);

        Assertions.assertEquals(1, actual.size());

        toDoService.deleteTodo(toDo);
    }

    @Test
    @DisplayName("Test get ToDo by null User and non empty Title ")
    public void checkGetByUserTitleWithNullUser() {
        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.getByUserTitle(null, "Title"));
    }

    @Test
    @DisplayName("Test get ToDo by User and  empty Title ")
    public void checkGetByUserTitleWithEmptyTitle() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);
        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.getByUserTitle(user, ""));

    }

    @Test
    @DisplayName("Test get Non existed ToDo by User and Title ")
    public void checkGetNonExistedToDoByUserTitle() {
        User user = new User();
        user.setEmail("email@gmail.com");

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.getByUserTitle(user, "TitleTest"));

    }

    @Test
    @DisplayName("Test get valid ToDo by User and Title ")
    public void checkGetValidToDoByUserTitle() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);

        ToDo toDo = new ToDo("Test #1", user);
        toDoService.addTodo(toDo, user);

        ToDo actual = toDoService.getByUserTitle(user, "Test #1");

        Assertions.assertEquals(toDo, actual);
        Assertions.assertEquals(user, actual.getOwner());
        Assertions.assertEquals(toDo.getTitle(), actual.getTitle());

        toDoService.deleteTodo(toDo);


    }


}

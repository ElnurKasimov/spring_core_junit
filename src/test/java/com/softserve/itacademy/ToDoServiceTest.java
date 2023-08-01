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

import java.util.List;

@RunWith(JUnitPlatform.class)
public class ToDoServiceTest {
    private static UserService userService;
    private static ToDoServiceImpl toDoService;


    @BeforeAll
    public static void setupBeforeClass() {
        AnnotationConfigApplicationContext annotationConfigContext = new AnnotationConfigApplicationContext(Config.class);
        userService = annotationConfigContext.getBean(UserService.class);
        annotationConfigContext.close();

        toDoService = new ToDoServiceImpl(userService);


    }

    @Test
    @DisplayName("Test adding new ToDo with null")
    public void checkAddToDoWithNullUser() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);
        ToDo toDo = new ToDo("ToDo #1", user);

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.addTodo(toDo, null));
        userService.deleteUser(user);
    }

    @Test
    @DisplayName("Test adding existed ToDo")
    public void checkAddExistedToDo() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);

        ToDo toDo = new ToDo("ToDo #1", user);
        toDoService.addTodo(toDo, user);

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.addTodo(toDo, user));

        toDoService.deleteTodo(toDo);
        userService.deleteUser(user);
    }

    @Test
    @DisplayName("Test adding ToDo with another User")
    public void checkAddToDoWithAnotherUser() {
        User oldUser = new User();
        oldUser.setEmail("email@gmail.com");
        userService.addUser(oldUser);

        User newUser = new User();
        newUser.setEmail("new_email@email.com");

        ToDo toDo = new ToDo("ToDo #1", oldUser);

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.addTodo(toDo, newUser));

        userService.deleteUser(oldUser);
    }

    @Test
    @DisplayName("Test adding new ToDo with new User")
    public void checkAddValidToDo() {
        User oldUser = new User();
        oldUser.setEmail("email@gmail.com");
        userService.addUser(oldUser);

        User newUser = new User();
        newUser.setEmail("new@email.com");
        userService.addUser(newUser);

        ToDo toDo = new ToDo("ToDo #1", oldUser);

        ToDo actual = toDoService.addTodo(toDo, newUser);
        ToDo expected = toDoService.getAll().stream()
                .filter(toDoFromList -> toDoFromList.equals(toDo))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected, actual);

        toDoService.deleteTodo(toDo);
        userService.deleteUser(oldUser);
        userService.deleteUser(newUser);
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
        user.setEmail("email@email.com");
        userService.addUser(user);
        ToDo toDo = new ToDo("Task #1", user);

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.updateTodo(toDo));

        userService.deleteUser(user);
    }

    @Test
    @DisplayName("Test update valid ToDo")
    public void checkUpdateValidToDo() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);

        ToDo newToDo = new ToDo("ToDo #1", user);

        Task taskNew = new Task("Task #1", Priority.LOW);
        newToDo.setTasks(List.of(taskNew));

        ToDo oldToDo = new ToDo("ToDo #1", user);

        Task taskOld = new Task("Task #3", Priority.MEDIUM);
        oldToDo.setTasks(List.of(taskOld));

        toDoService.addTodo(oldToDo, user);


        ToDo actual = toDoService.updateTodo(newToDo);

        Assertions.assertEquals(List.of(taskNew), actual.getTasks());

        userService.deleteUser(user);
        toDoService.deleteTodo(oldToDo);

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

        ToDo toDo = new ToDo("ToDo #5", user);

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.deleteTodo(toDo));

        userService.deleteUser(user);
    }

    @Test
    @DisplayName("Test delete valid ToDo")
    public void checkDeleteValidToDo() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);

        ToDo toDo = new ToDo("ToDo #1", user);
        toDoService.addTodo(toDo, user);

        toDoService.deleteTodo(toDo);

        Assertions.assertFalse(toDoService.getAll().contains(toDo));
        userService.deleteUser(user);

    }

    @Test
    @DisplayName("Test get All ToDo")
    public void checkGetAllToDo() {
        User firstUser = new User();
        firstUser.setEmail("first_email@gmail.com");

        User secondUser = new User();
        secondUser.setEmail("second_email@gmail.com");

        userService.addUser(firstUser);
        userService.addUser(secondUser);

        ToDo firstToDo = new ToDo("ToDo #1", firstUser);
        ToDo secondToDo = new ToDo("ToDo #2", secondUser);

        toDoService.addTodo(firstToDo, firstUser);
        toDoService.addTodo(secondToDo, secondUser);

        List<ToDo> list = List.of(firstToDo, secondToDo);

        Assertions.assertEquals(list, toDoService.getAll());

        toDoService.deleteTodo(firstToDo);
        toDoService.deleteTodo(secondToDo);
        userService.deleteUser(firstUser);
        userService.deleteUser(secondUser);
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
        user.setEmail("email@gmail.com");
        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.getByUser(user));
    }

    @Test
    @DisplayName("Test get ToDo by  User ")
    public void checkGetToDoByUser() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);

        ToDo toDo = new ToDo("ToDo #1", user);
        toDoService.addTodo(toDo, user);

        List<ToDo> actual = toDoService.getByUser(user);

        Assertions.assertEquals(1, actual.size());

        toDoService.deleteTodo(toDo);
        userService.deleteUser(user);
    }

    @Test
    @DisplayName("Test get ToDo by null User and non empty Title ")
    public void checkGetByUserTitleWithNullUser() {
        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.getByUserTitle(null, "Title"));
    }

    @Test
    @DisplayName("Test get ToDo by User and empty Title ")
    public void checkGetByUserTitleWithEmptyTitle() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);
        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.getByUserTitle(user, ""));

        userService.deleteUser(user);

    }

    @Test
    @DisplayName("Test get Non existed ToDo by User and Title ")
    public void checkGetNonExistedToDoByUserTitle() {
        User user = new User();
        user.setEmail("email@gmail.com");

        Assertions.assertThrows(ToDoNotFoundException.class, () -> toDoService.getByUserTitle(user, "Title"));

    }

    @Test
    @DisplayName("Test get valid ToDo by User and Title ")
    public void checkGetValidToDoByUserTitle() {
        User user = new User();
        user.setEmail("email@gmail.com");
        userService.addUser(user);

        ToDo toDo = new ToDo("ToDo #1", user);
        toDoService.addTodo(toDo, user);

        ToDo actual = toDoService.getByUserTitle(user, "ToDo #1");

        Assertions.assertEquals(toDo, actual);
        Assertions.assertEquals(user, actual.getOwner());
        Assertions.assertEquals(toDo.getTitle(), actual.getTitle());

        toDoService.deleteTodo(toDo);
        userService.deleteUser(user);


    }


}

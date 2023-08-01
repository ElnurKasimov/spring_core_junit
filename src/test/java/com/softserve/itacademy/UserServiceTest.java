package com.softserve.itacademy;

import com.softserve.itacademy.exceptions.UserNotFoundException;
import com.softserve.itacademy.model.ToDo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnitPlatform.class)
public class UserServiceTest {
    private static UserService userService;

    @BeforeAll
    public static void setupBeforeClass() throws Exception {
        AnnotationConfigApplicationContext annotationConfigContext = new AnnotationConfigApplicationContext(Config.class);
        userService = annotationConfigContext.getBean(UserService.class);
        annotationConfigContext.close();
    }

    @Test
    @DisplayName("Check adding correct user")
    public void checkAddUser() {
        userService.getAll().clear();
        User user = new User("firstName", "lastName","email","password");
        user.setFirstName("Michael");
        user.setLastName("Romanenko");
        user.setEmail("bulldog21@gmail.com");
        user.setPassword("321321");
        User expected = new User("Michael", "Romanenko", "bulldog21@gmail.com", "321321");
        User actual = userService.addUser(user);
        Assertions.assertEquals(expected, actual);
        userService.getAll().clear();
    }
    @Test
    @DisplayName("Check adding null user")
    public void checkAddNullUser() {
        User user = null;
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.addUser(user));
    }
    @Test
    @DisplayName("Check adding existing user")
    public void checkExistingUser() {
        User user = new User("Michael", "Romanenko", "bulldog21@gmail.com", "321321");
        userService.addUser(user);
        User user2 = new User("Michael", "Romanenko", "bulldog21@gmail.com", "321321");
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.addUser(user));
    }
    @Test
    @DisplayName("Check update User with null")
    public void checkUpdateNullUser() {
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updateUser(null));
    }
    @Test
    @DisplayName("Check update non existed User")
    public void checkUpdateNonExistedUser() {
        User user = new User();
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
    }
    @Test
    @DisplayName("Check update valid User")
    public void checkUpdateValidUser() {
        User actual = new User("firstName", "lastName","email","email");
        User excpected =  new User("firstName", "lastName","email","email");
        excpected.setMyTodos(new ArrayList<>(List.of(new ToDo("task#1", excpected), new ToDo("task#2", excpected))));

        excpected.setMyTodos(new ArrayList<>(){});

        userService.addUser(actual);
        userService.updateUser(excpected);

        Assertions.assertEquals(actual, excpected);
    }
    @Test
    @DisplayName("Check get all users")
    public void checkGetAllUsers() {
        userService.getAll().clear();
        User user1 =  new User("mykyta", "hahua","2gagua121@gmail.com","12345");
        user1.setMyTodos(new ArrayList<>(List.of(new ToDo("task#1", user1), new ToDo("task#2", user1))));

        User user2 =  new User("danylo", "shoroh","shoroh@gmail.com","54321");
        user2.setMyTodos(new ArrayList<>(List.of(new ToDo("task#1", user2), new ToDo("task#2", user2))));

        User user3 =  new User("oleg", "karp","oleg@gmail.com","11223344455");
        user3.setMyTodos(new ArrayList<>(List.of(new ToDo("task#1", user3), new ToDo("task#2", user3))));
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);


        List<User> expected = List.of(user1, user2, user3);

        Assertions.assertEquals(expected, userService.getAll());
    }

    @Test
    @DisplayName("Check delete user with null")
    public void checkDeleteUserWithNull() {
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteUser(null));
    }

    @Test
    @DisplayName("Check delete non existed user")
    public void checkDeleteNonExistedUser() {
        User user1 =  new User("mykyta", "hahua","2gagua121@gmail.com","12345");
        User user2 =  new User("danylo", "shoroh","shoroh@gmail.com","54321");
        userService.addUser(user1);

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteUser(user2));
    }

    @Test
    @DisplayName("Check delete valid user")
    public void checkDeleteValidUser() {
        User user =  new User("danylo", "shoroh","shoroh@gmail.com","54321");
        userService.addUser(user);
        userService.deleteUser(user);
        Assertions.assertFalse(userService.getAll().contains(user));
    }

}

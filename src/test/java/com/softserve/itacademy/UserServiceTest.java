package com.softserve.itacademy;

import com.softserve.itacademy.exceptions.UserIllegalArgumentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;

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
    }
    @Test
    @DisplayName("Check adding null user")
    public void checkAddNullUser() {
        User user = null;
        Assertions.assertThrows(UserIllegalArgumentException.class, () -> userService.addUser(user));
    }
    @Test
    @DisplayName("Check adding existing user")
    public void checkExistingUser() {
        User user = new User("Michael", "Romanenko", "bulldog21@gmail.com", "321321");
        userService.addUser(user);
        User user2 = new User("Michael", "Romanenko", "bulldog21@gmail.com", "321321");
        Assertions.assertThrows(UserIllegalArgumentException.class, () -> userService.addUser(user));
    }

}

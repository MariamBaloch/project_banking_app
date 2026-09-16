package com.ga.acme.services;

import com.ga.acme.enums.FilePath;
import com.ga.acme.enums.Roles;
import com.ga.acme.exceptions.AccountAlreadyExistsException;
import com.ga.acme.exceptions.AccountLockedException;
import com.ga.acme.exceptions.InvalidPasswordException;
import com.ga.acme.models.User;
import com.ga.acme.util.FileHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class AuthServiceTest {

    @Test
    public void encryptPasswordShouldReturnHashedPassword() {
        String hashedPassword = AuthService.encryptPassword("password");
        Assert.assertNotNull(hashedPassword);
        Assert.assertEquals(64, hashedPassword.length());
        Assert.assertEquals(hashedPassword, AuthService.encryptPassword("password"));
    }

    @Test
    public void checkPasswordShouldReturnTrueForCorrectPassword() {
        String hashedPassword = AuthService.encryptPassword("password");
        Assert.assertTrue(AuthService.checkPassword("password", hashedPassword));
    }

    @Test
    public void checkPasswordShouldReturnFalseForWrongPassword() {
        String hashedPassword = AuthService.encryptPassword("password");
        Assert.assertFalse(AuthService.checkPassword("123", hashedPassword));
    }

    @Test
    public void signupShouldCreateUserSuccessfully() throws AccountAlreadyExistsException {
        String id = "test" + UUID.randomUUID().toString().substring(0, 8);
        String name = "test";
        String password = "password";

        User user = AuthService.signup(id, name, password, Roles.CUSTOMER);
        Assert.assertNotNull(user);
        Assert.assertEquals(id, user.getId());
        Assert.assertEquals(name, user.getName());
        Assert.assertEquals(AuthService.encryptPassword(password), user.getHashedPassword());
    }

    @Test(expected = AccountAlreadyExistsException.class)
    public void signupShouldThrowExceptionWhenUserAlreadyExists() throws Exception {
        String id = "test";
        String name = "test";
        String password = "password";

        AuthService.signup(id, name, password, Roles.CUSTOMER);
        AuthService.signup(id, name, password, Roles.CUSTOMER);
    }

    @Test
    public void loginWithCorrectPasswordShouldLogInSuccessfully() throws Exception {
        String id = "test";
        String password = "password";

        //needed to unlock account after running other tests
        User userFromFile = UserService.getUserById(id);
        userFromFile.setLockedUntil(null);
        userFromFile.setLoginAttempts(0);
        FileHandler.writeToFile(FilePath.USERS.getPath(), userFromFile);

        User user = AuthService.login(id, password);

        Assert.assertNotNull(user);
        Assert.assertEquals(id, user.getId());
    }

    @Test(expected = InvalidPasswordException.class)
    public void loginWithWrongPasswordShouldThrowInvalidPasswordExceptionIfAccountNotLocked() throws Exception {

        String id = "test";
        String validPassword = "password";
        String invalidPassword = "invalidPassword";

        User userFromFile = UserService.getUserById(id);
        userFromFile.setLockedUntil(null);
        userFromFile.setLoginAttempts(0);
        FileHandler.writeToFile(FilePath.USERS.getPath(), userFromFile);

        AuthService.login(id, invalidPassword);
    }

    @Test(expected = AccountLockedException.class)
    public void loginWithWrongPasswordThreeTimesShouldThrowAccountLockedException() throws Exception {

        String id = "test";
        String validPassword = "password";
        String invalidPassword = "invalidPassword";

        AuthService.login("test", validPassword);
        try {
            AuthService.login(id, invalidPassword);
        } catch (InvalidPasswordException e) {
            System.out.println(e.getMessage());
        }
        try {
            AuthService.login(id, invalidPassword);
        } catch (InvalidPasswordException e) {
            System.out.println(e.getMessage());
        }
        AuthService.login(id, invalidPassword);
    }

}
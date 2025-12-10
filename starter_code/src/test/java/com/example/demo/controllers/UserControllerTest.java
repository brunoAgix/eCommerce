package com.example.demo.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepo=mock(UserRepository.class);

    private CartRepository cartRepo=mock(CartRepository.class);

    private BCryptPasswordEncoder encoder=mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();

        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        User u = response.getBody();

        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test",u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void create_user_password_too_short() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("short");  // less than 7 characters
        r.setConfirmPassword("short");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void create_user_password_mismatch() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("differentPassword");  // doesn't match

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ==================== findById tests ====================

    @Test
    public void find_by_id_happy_path() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("hashedPassword");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(1L, u.getId());
        assertEquals("testUser", u.getUsername());
    }

    @Test
    public void find_by_id_not_found() {
        when(userRepo.findById(999L)).thenReturn(Optional.empty());

        final ResponseEntity<User> response = userController.findById(999L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ==================== findByUserName tests ====================

    @Test
    public void find_by_username_happy_path() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("hashedPassword");

        when(userRepo.findByUsername("testUser")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("testUser");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals("testUser", u.getUsername());
    }

    @Test
    public void find_by_username_not_found() {
        when(userRepo.findByUsername("nonExistentUser")).thenReturn(null);

        final ResponseEntity<User> response = userController.findByUserName("nonExistentUser");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}

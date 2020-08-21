package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void create_user_error_path() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("testpassword");
        userRequest.setConfirmPassword("testpassword!");

        ResponseEntity<User> response = userController.createUser(userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void find_user_happy_path() {
        User user = new User();
        user.setId(Long.valueOf(1));
        user.setUsername("test");
        user.setPassword("password");

        when(userRepository.findByUsername("test")).thenReturn(user);

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("testpassword");
        userRequest.setConfirmPassword("testpassword");

        userController.createUser(userRequest);

        ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test", user.getUsername());
    }

    @Test
    public void find_user_error_path() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("testpassword");
        userRequest.setConfirmPassword("testpassword");

        ResponseEntity<User> findUser = userController.findByUserName("test_user");

        assertNotNull(userController.findByUserName("test_user"));
        assertEquals(HttpStatus.NOT_FOUND, findUser.getStatusCode());
    }

    @Test
    public void find_by_id_happy_path() {
        User user = new User();
        user.setId(1);
        user.setUsername("test");
        user.setPassword("testpassword");
        when(userRepository.findById((long)1)).thenReturn(java.util.Optional.of(user));

        ResponseEntity<User> response = userController.findById((long)1);

        assertNotNull(response);
        assertEquals("test", response.getBody().getUsername());
    }

}

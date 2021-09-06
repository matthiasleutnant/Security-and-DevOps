package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserControllerTest {
    @Autowired
    private UserController userController;

    @MockBean
    private BCryptPasswordEncoder encoder;

    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String HASHEDPASSWORD = "THISISHASHED";

    @Test
    public void createUser_HappyPath(){
        when(encoder.encode(PASSWORD)).thenReturn(HASHEDPASSWORD);


        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword(PASSWORD);

        ResponseEntity<User> response= userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        User user = response.getBody();

        assertNotNull(user);
        assertEquals(1,user.getId());
        assertEquals(USERNAME,user.getUsername());
        assertEquals(HASHEDPASSWORD,user.getPassword());
    }

    @Test
    public void createUser_wrong_Confirm_Password(){
        when(encoder.encode(PASSWORD)).thenReturn(HASHEDPASSWORD);


        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword(PASSWORD+"1");

        ResponseEntity<User> response= userController.createUser(createUserRequest);

        assertEquals(HttpStatus.BAD_REQUEST.value(),response.getStatusCodeValue());
        assertThat(response.getBody()).isNull();
    }
}

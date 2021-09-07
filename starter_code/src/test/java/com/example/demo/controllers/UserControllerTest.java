package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class UserControllerTest {
    @Autowired
    private UserController userController;

    @MockBean
    private BCryptPasswordEncoder encoder;

    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String HASHEDPASSWORD = "THISISHASHED";

    @BeforeEach
    private void prep(){
        when(encoder.encode(PASSWORD)).thenReturn(HASHEDPASSWORD);
    }

    @Test
    public void createUser_HappyPath(){
        ResponseEntity<User> response = CreateUserAndGetUserResponseEntity();
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(USERNAME,user.getUsername());
        assertEquals(HASHEDPASSWORD,user.getPassword());
    }

    private ResponseEntity<User> CreateUserAndGetUserResponseEntity() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword(PASSWORD);

        return userController.createUser(createUserRequest);
    }

    @Test
    public void createUser_wrong_Confirm_Password(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword(PASSWORD+"1");

        ResponseEntity<User> response= userController.createUser(createUserRequest);

        assertEquals(HttpStatus.BAD_REQUEST.value(),response.getStatusCodeValue());
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void findByUserName_happy_Case(){
        CreateUserAndGetUserResponseEntity();
        ResponseEntity<User> response = userController.findByUserName(USERNAME);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUsername()).isEqualTo(USERNAME);
        assertThat(response.getBody().getPassword()).isEqualTo(HASHEDPASSWORD);
    }

    @Test
    public void findByUserName_not_found(){
        CreateUserAndGetUserResponseEntity();
        ResponseEntity<User> response = userController.findByUserName(USERNAME+"1");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void findById_happy_Case(){
        ResponseEntity<User> userResponse = CreateUserAndGetUserResponseEntity();
        ResponseEntity<User> response = userController.findById(userResponse.getBody().getId());
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUsername()).isEqualTo(USERNAME);
        assertThat(response.getBody().getPassword()).isEqualTo(HASHEDPASSWORD);
    }

    @Test
    public void findById_not_found(){
        ResponseEntity<User> userResponse = CreateUserAndGetUserResponseEntity();
        ResponseEntity<User> response = userController.findById(userResponse.getBody().getId()+1);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}

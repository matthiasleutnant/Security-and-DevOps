package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class OrderControllerTest {
    @Autowired
    OrderController orderController;
    @Autowired
    UserController userController;
    @Autowired
    CartController cartController;

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
    public void submit_happy_path(){
        CreateUserAndGetUserResponseEntity();
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setQuantity(1);
        request.setUsername(USERNAME);
        cartController.addToCart(request);
        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
    }

    private ResponseEntity<User> CreateUserAndGetUserResponseEntity() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword(PASSWORD);

        return userController.createUser(createUserRequest);
    }
}

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    private void prep() {
        when(encoder.encode(PASSWORD)).thenReturn(HASHEDPASSWORD);
        CreateUserAndGetUserResponseEntity();
    }

    @Test
    public void submit_happy_path() {
        ResponseEntity<UserOrder> response = getUserOrderResponseEntity();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<UserOrder> getUserOrderResponseEntity() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setQuantity(1);
        request.setUsername(USERNAME);
        cartController.addToCart(request);
        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        return response;
    }

    @Test
    public void submit_user_unknown() {
        ResponseEntity<UserOrder> response = orderController.submit(USERNAME + "1");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getOrdersForUser_happy_path() {
        for (int i = 0; i < 5; i++) {
            getUserOrderResponseEntity();
        }
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(5);
    }

    @Test
    public void getOrdersForUser_user_does_not_exist() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME+"1");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<User> CreateUserAndGetUserResponseEntity() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword(PASSWORD);

        return userController.createUser(createUserRequest);
    }
}

package com.example.demo.controllers;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class CartControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    CartController cartController;

    @MockBean
    private BCryptPasswordEncoder encoder;

    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String HASHEDPASSWORD = "THISISHASHED";
    private User user;

    @BeforeEach
    private void prep() {
        when(encoder.encode(PASSWORD)).thenReturn(HASHEDPASSWORD);
        user = CreateUserAndGetUserResponseEntity().getBody();
    }

    @Test
    public void addToCart_happy_case() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setUsername(user.getUsername());
        request.setQuantity(10);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getItems().size()).isEqualTo(10);
        assertThat(response.getBody().getUser().getUsername()).isEqualTo(USERNAME);
    }

    @Test
    public void addToCart_no_user() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setUsername(user.getUsername() + "1");
        request.setQuantity(10);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void addToCart_no_item() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(10);
        request.setUsername(user.getUsername());
        request.setQuantity(10);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void removeFromCart_happy_case() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setUsername(user.getUsername());
        request.setQuantity(10);
        cartController.addToCart(request);
        request.setQuantity(4);
        ResponseEntity<Cart> response = cartController.removeFromCart(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getItems().size()).isEqualTo(6);
    }

    @Test
    public void removeFromCart_wrong_username() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setUsername(user.getUsername());
        request.setQuantity(10);
        cartController.addToCart(request);
        request.setQuantity(4);
        request.setUsername(USERNAME+"1");
        ResponseEntity<Cart> response = cartController.removeFromCart(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void removeFromCart_wrong_item() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setUsername(user.getUsername());
        request.setQuantity(10);
        cartController.addToCart(request);
        request.setQuantity(4);
        request.setItemId(41);
        ResponseEntity<Cart> response = cartController.removeFromCart(request);
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

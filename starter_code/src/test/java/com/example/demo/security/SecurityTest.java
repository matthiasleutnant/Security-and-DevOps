package com.example.demo.security;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {
    @Autowired
    private MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void Login_happy_case() throws Exception {
        String username = "Matthias";
        String password = "password";
        sendCreateUserRequest(username, password).andExpect(status().isOk());
        String auth = loginUserAndGetAuth(username, password);

        sendCartRequestWithAuthentication(username, auth).andExpect(status().isOk());
    }

    @Test
    public void Login_wrong_auth() throws Exception {
        String username = "Matze";
        String password = "password";
        sendCreateUserRequest(username, password).andExpect(status().isOk());
        String auth = loginUserAndGetAuth(username, password);

        sendCartRequestWithAuthentication(username, auth+"0");
    }

    private ResultActions sendCartRequestWithAuthentication(String username, String auth) throws Exception {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(username);
        request.setQuantity(1);
        request.setItemId(1);
        return mvc.perform(
                post(new URI("/api/cart/addToCart"))
                        .header("Authorization", auth)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private String loginUserAndGetAuth(String username, String password) throws Exception {
        ResultActions loginResponse = sendUserLoginRequest(username, password);
        return loginResponse.andReturn().getResponse().getHeader("Authorization");
    }

    private ResultActions sendUserLoginRequest(String username, String password) throws Exception {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return mvc.perform(
                post(new URI("/login"))
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions sendCreateUserRequest(String username, String password) throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setConfirmPassword(password);
        return mvc.perform(
                post(new URI("/api/user/create"))
                        .content(objectMapper.writeValueAsString(userRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}

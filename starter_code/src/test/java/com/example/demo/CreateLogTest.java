package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

@SpringBootTest
@Transactional
public class CreateLogTest {

    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<String> firstnames = new ArrayList<>();
    private ArrayList<String> lastnames = new ArrayList<>();
    private BufferedReader firstNameReader;
    private BufferedReader lastNameReader;

    @Autowired
    UserController userController;

    @Autowired
    CartController cartController;

    @Autowired
    OrderController orderController;


    public void createLog(){
        try {
            firstNameReader = new BufferedReader(new FileReader("src/test/resources/firstnames.txt"));
            lastNameReader = new BufferedReader(new FileReader("src/test/resources/lastnames.txt"));
            while(firstNameReader.ready()){
                firstnames.add(firstNameReader.readLine());
            }
            while(lastNameReader.ready()){
                lastnames.add(lastNameReader.readLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        createNewUser();
        for(int i=0;i<100000;i++){
            randomAction();
        }
    }

    private void createNewUser(){
        byte[] array = new byte[16];
        new Random().nextBytes(array);
        String password = new String(array, Charset.forName("UTF-8"));
        CreateUserRequest createUserRequest = new CreateUserRequest();
        String firstname=firstnames.get((int)(randomInt(firstnames.size())));
        String lastname=lastnames.get((int)(randomInt(lastnames.size())));
        createUserRequest.setUsername(firstname +" "+lastname);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(password);
        users.add(userController.createUser(createUserRequest).getBody());
    }

    private void addRandomItemToRandomUser(){
        String username = users.get(randomInt(users.size())).getUsername();
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(randomInt(1,2));
        request.setQuantity(randomInt(1,5));
        request.setUsername(username);
        cartController.addToCart(request);
        if(Math.random()<0.5){
            orderController.submit(username);
        }
    }

    private void randomAction(){
        int number = randomInt(2);
        switch (number){
            case 0:
                createNewUser();
                break;
            case 1:
                addRandomItemToRandomUser();
                break;
        }
    }


    private int randomInt(int upperLimit){
        return (int)(Math.random()*upperLimit);
    }

    private int randomInt(int lowerLimit, int upperLimit){
        return ((int)(Math.random()*upperLimit))+lowerLimit;
    }
}

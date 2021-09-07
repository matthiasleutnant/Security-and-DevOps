package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ItemControllerTest {
    @Autowired
    ItemController itemController;

    @Test
    public void getItems_happyPath(){
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(2);
    }

    @Test
    public void getItemById_happyPath(){
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(1);
    }

    @Test
    public void getItemById_not_available(){
        ResponseEntity<Item> response = itemController.getItemById(5L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getItemsByName_happy_path_single_result(){
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void getItemsByName_no_result(){
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Triangle Widget");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

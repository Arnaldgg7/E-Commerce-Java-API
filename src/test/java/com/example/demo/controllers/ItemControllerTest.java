package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private Item item;

    private List<Item> items = new ArrayList<>();


    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        item = new Item();
        item.setId(Long.valueOf(0));
        item.setName("Item name");
        item.setPrice(new BigDecimal(7.99));
        item.setDescription("This is an item description");

        items.add(item);
        items.add(item);
        items.add(item);
    }

    @Test
    public void get_all_items() {
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
    }

    @Test
    public void get_item_by_id_happy_path(){
        when(itemRepository.findById(Long.valueOf(0))).thenReturn(java.util.Optional.ofNullable(item));
        ResponseEntity<Item> response = itemController.getItemById(item.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(item.getName(), response.getBody().getName());
    }

    @Test
    public void get_item_by_id_not_found(){
        ResponseEntity<Item> response = itemController.getItemById(item.getId());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void get_items_by_name_not_found(){
        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void get_items_by_name() {
        when(itemRepository.findByName("Item name")).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
    }
}

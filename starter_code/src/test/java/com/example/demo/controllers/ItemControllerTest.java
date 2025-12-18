package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);

    @BeforeEach
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    private Item createTestItem(Long id, String name, BigDecimal price) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setDescription("A widget that is " + name.toLowerCase());
        return item;
    }

    // ==================== getItems tests ====================

    @Test
    public void get_all_items_happy_path() {
        Item item1 = createTestItem(1L, "Round Widget", BigDecimal.valueOf(2.99));
        Item item2 = createTestItem(2L, "Square Widget", BigDecimal.valueOf(1.99));
        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepo.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> returnedItems = response.getBody();
        assertNotNull(returnedItems);
        assertEquals(2, returnedItems.size());
        assertEquals("Round Widget", returnedItems.get(0).getName());
        assertEquals("Square Widget", returnedItems.get(1).getName());
    }

    @Test
    public void get_all_items_empty() {
        when(itemRepo.findAll()).thenReturn(new ArrayList<>());

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> returnedItems = response.getBody();
        assertNotNull(returnedItems);
        assertTrue(returnedItems.isEmpty());
    }

    // ==================== getItemById tests ====================

    @Test
    public void get_item_by_id_happy_path() {
        Item item = createTestItem(1L, "Round Widget", BigDecimal.valueOf(2.99));

        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Item returnedItem = response.getBody();
        assertNotNull(returnedItem);
        assertEquals(1L, returnedItem.getId());
        assertEquals("Round Widget", returnedItem.getName());
        assertEquals(BigDecimal.valueOf(2.99), returnedItem.getPrice());
    }

    @Test
    public void get_item_by_id_not_found() {
        when(itemRepo.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(999L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ==================== getItemsByName tests ====================

    @Test
    public void get_items_by_name_happy_path() {
        Item item1 = createTestItem(1L, "Widget", BigDecimal.valueOf(2.99));
        Item item2 = createTestItem(2L, "Widget", BigDecimal.valueOf(3.99));
        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepo.findByName("Widget")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Widget");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> returnedItems = response.getBody();
        assertNotNull(returnedItems);
        assertEquals(2, returnedItems.size());
    }

    @Test
    public void get_items_by_name_not_found() {
        when(itemRepo.findByName("NonExistentItem")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("NonExistentItem");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void get_items_by_name_empty_list() {
        when(itemRepo.findByName("NonExistentItem")).thenReturn(new ArrayList<>());

        ResponseEntity<List<Item>> response = itemController.getItemsByName("NonExistentItem");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

}

package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);

    @BeforeEach
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("hashedPassword");
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);
        return user;
    }

    private Item createTestItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");
        return item;
    }

    // ==================== addToCart tests ====================

    @Test
    public void add_to_cart_happy_path() {
        User user = createTestUser();
        Item item = createTestItem();

        when(userRepo.findByUsername("testUser")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(2, cart.getItems().size());
        verify(cartRepo, times(1)).save(cart);
    }

    @Test
    public void add_to_cart_user_not_found() {
        when(userRepo.findByUsername("nonExistentUser")).thenReturn(null);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonExistentUser");
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void add_to_cart_item_not_found() {
        User user = createTestUser();

        when(userRepo.findByUsername("testUser")).thenReturn(user);
        when(itemRepo.findById(999L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(999L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ==================== removeFromCart tests ====================

    @Test
    public void remove_from_cart_happy_path() {
        User user = createTestUser();
        Item item = createTestItem();

        // Add item to cart first
        user.getCart().addItem(item);
        user.getCart().addItem(item);

        when(userRepo.findByUsername("testUser")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        verify(cartRepo, times(1)).save(cart);
    }

    @Test
    public void remove_from_cart_user_not_found() {
        when(userRepo.findByUsername("nonExistentUser")).thenReturn(null);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonExistentUser");
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void remove_from_cart_item_not_found() {
        User user = createTestUser();

        when(userRepo.findByUsername("testUser")).thenReturn(user);
        when(itemRepo.findById(999L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(999L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

}

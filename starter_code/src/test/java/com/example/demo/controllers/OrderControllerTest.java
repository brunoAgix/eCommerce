package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepo = mock(UserRepository.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);

    @BeforeEach
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    private User createTestUserWithCart() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("hashedPassword");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        // Add items to cart
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Round Widget");
        item1.setPrice(BigDecimal.valueOf(2.99));
        item1.setDescription("A widget that is round");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Square Widget");
        item2.setPrice(BigDecimal.valueOf(1.99));
        item2.setDescription("A widget that is square");

        cart.addItem(item1);
        cart.addItem(item2);
        user.setCart(cart);

        return user;
    }

    // ==================== submit order tests ====================

    @Test
    public void submit_order_happy_path() {
        User user = createTestUserWithCart();

        when(userRepo.findByUsername("testUser")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("testUser");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(2, order.getItems().size());
        assertEquals(BigDecimal.valueOf(4.98), order.getTotal());

        verify(orderRepo, times(1)).save(any(UserOrder.class));
    }

    @Test
    public void submit_order_user_not_found() {
        when(userRepo.findByUsername("nonExistentUser")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("nonExistentUser");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(orderRepo, never()).save(any(UserOrder.class));
    }

    // ==================== getOrdersForUser tests ====================

    @Test
    public void get_orders_for_user_happy_path() {
        User user = createTestUserWithCart();

        // Create order history
        UserOrder order1 = UserOrder.createFromCart(user.getCart());
        UserOrder order2 = UserOrder.createFromCart(user.getCart());
        List<UserOrder> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(userRepo.findByUsername("testUser")).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<UserOrder> returnedOrders = response.getBody();
        assertNotNull(returnedOrders);
        assertEquals(2, returnedOrders.size());
    }

    @Test
    public void get_orders_for_user_empty_history() {
        User user = createTestUserWithCart();

        when(userRepo.findByUsername("testUser")).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(new ArrayList<>());

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<UserOrder> returnedOrders = response.getBody();
        assertNotNull(returnedOrders);
        assertTrue(returnedOrders.isEmpty());
    }

    @Test
    public void get_orders_for_user_not_found() {
        when(userRepo.findByUsername("nonExistentUser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("nonExistentUser");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

}

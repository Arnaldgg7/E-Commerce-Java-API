package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private Cart cart;

    private User user;

    private List<UserOrder> orders = new ArrayList<>();

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        user = new User();
        user.setId(Long.valueOf(0));
        user.setUsername("user");
        user.setPassword("pw");

        Item item = new Item();
        item.setId(Long.valueOf(0));
        item.setDescription("A very cool item");
        item.setName("Cool-item");
        item.setPrice(new BigDecimal(18));

        cart = new Cart();
        cart.setId(Long.valueOf(0));
        cart.addItem(item);
        cart.addItem(item);
        cart.addItem(item);
        cart.setUser(user);

        user.setCart(cart);

        when(userRepository.findByUsername("user")).thenReturn(user);
    }

    @Test
    public void submit_order_happy_path(){
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals((18 * 3), response.getBody().getTotal().intValue());
    }

    @Test
    public void get_orders_for_user_happy_path() {
        ResponseEntity<UserOrder> orderResponse = orderController.submit(user.getUsername());
        orders.add(orderResponse.getBody());
        when(orderRepository.findByUser(user)).thenReturn(orders);

        orderController.submit(user.getUsername());
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(response);
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void submit_order_not_found(){
        ResponseEntity<UserOrder> response = orderController.submit("notFound");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void get_orders_for_user_not_found(){
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("notFound");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}

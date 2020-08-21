package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class CartControllerTest {
    private CartController cartController;

    private CartRepository cartRepository = mock(CartRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private User testUser;

    private Item testItem;

    private ModifyCartRequest modifyCartRequest;

    @Before
    public void setup(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        testUser = new User();
        testUser.setId(Long.valueOf(0));
        testUser.setUsername("testUser");

        Cart cart = new Cart();
        cart.setUser(testUser);
        cart.setId(Long.valueOf(0));

        testItem = new Item();
        testItem.setId(Long.valueOf(0));
        testItem.setName("TestItem");
        testItem.setPrice(new BigDecimal(7.99));

        cart.addItem(testItem);
        cart.addItem(testItem);
        testUser.setCart(cart);

        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(Long.valueOf(0));
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(testUser);
        when(itemRepository.findById(Long.valueOf(0))).thenReturn(java.util.Optional.ofNullable(testItem));
    }

    @Test
    public void add_to_cart_happy_path() {
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void remove_from_cart_happy_path() {
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void add_to_cart_id_not_found(){
        modifyCartRequest.setItemId(Long.valueOf(1));
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void add_to_cart_user_not_found(){
        modifyCartRequest.setUsername("some name");
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void remove_from_cart_id_not_found(){
        modifyCartRequest.setItemId(Long.valueOf(1));
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

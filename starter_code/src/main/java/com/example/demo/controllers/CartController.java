package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private static final Logger log = Logger.getLogger(CartController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		try {
			log.info("AddToCart request received for user: " + request.getUsername() + ", itemId: " + request.getItemId() + ", quantity: " + request.getQuantity());

			User user = userRepository.findByUsername(request.getUsername());
			if (user == null) {
				log.error("AddToCart FAILURE - User not found: " + request.getUsername());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			Optional<Item> item = itemRepository.findById(request.getItemId());
			if (!item.isPresent()) {
				log.error("AddToCart FAILURE - Item not found: " + request.getItemId());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity())
					.forEach(i -> cart.addItem(item.get()));
			cartRepository.save(cart);

			log.info("AddToCart SUCCESS - Added " + request.getQuantity() + " x " + item.get().getName() + " to cart for user: " + request.getUsername());
			return ResponseEntity.ok(cart);

		} catch (Exception e) {
			log.error("AddToCart EXCEPTION - Error adding to cart for user: " + request.getUsername() + " - Exception: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		try {
			log.info("RemoveFromCart request received for user: " + request.getUsername() + ", itemId: " + request.getItemId() + ", quantity: " + request.getQuantity());

			User user = userRepository.findByUsername(request.getUsername());
			if (user == null) {
				log.error("RemoveFromCart FAILURE - User not found: " + request.getUsername());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			Optional<Item> item = itemRepository.findById(request.getItemId());
			if (!item.isPresent()) {
				log.error("RemoveFromCart FAILURE - Item not found: " + request.getItemId());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity())
					.forEach(i -> cart.removeItem(item.get()));
			cartRepository.save(cart);

			log.info("RemoveFromCart SUCCESS - Removed " + request.getQuantity() + " x " + item.get().getName() + " from cart for user: " + request.getUsername());
			return ResponseEntity.ok(cart);

		} catch (Exception e) {
			log.error("RemoveFromCart EXCEPTION - Error removing from cart for user: " + request.getUsername() + " - Exception: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
		
}

package com.example.demo.controllers;

import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {


	private static final Logger log = Logger.getLogger(OrderController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		try {
			log.info("Order request received for username: " + username);

			User user = userRepository.findByUsername(username);
			if (user == null) {
				log.error("Order FAILURE - User not found: " + username);
				return ResponseEntity.notFound().build();
			}

			UserOrder order = UserOrder.createFromCart(user.getCart());
			orderRepository.save(order);

			log.info("Order SUCCESS - Order submitted for user: " + username + " with " + order.getItems().size() + " items, total: " + order.getTotal());
			return ResponseEntity.ok(order);

		} catch (Exception e) {
			log.error("Order EXCEPTION - Error submitting order for user: " + username + " - Exception: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		try {
			User user = userRepository.findByUsername(username);
			if (user == null) {
				log.warn("OrderHistory FAILURE - User not found: " + username);
				return ResponseEntity.notFound().build();
			}

			List<UserOrder> orders = orderRepository.findByUser(user);
			log.info("OrderHistory SUCCESS - Retrieved " + orders.size() + " orders for user: " + username);
			return ResponseEntity.ok(orders);

		} catch (Exception e) {
			log.error("OrderHistory EXCEPTION - Error retrieving order history for user: " + username + " - Exception: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}

package com.example.babyproducts.resource;


import com.example.babyproducts.model.OrderDetails;
import com.example.babyproducts.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
public class OrderResource {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<OrderDetails> createOrder(@RequestParam("file") MultipartFile file, @RequestParam("items") List<String> items) {

        return  ResponseEntity.ok(orderService.createOrder( file, items));

    }
}

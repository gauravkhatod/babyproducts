package com.example.babyproducts.service;


import com.example.babyproducts.model.OrderDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrderService {


     OrderDetails createOrder(MultipartFile file, List<String> items);




}

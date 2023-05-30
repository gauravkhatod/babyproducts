package com.example.babyproducts.service;


import com.example.babyproducts.model.Order;
import com.example.babyproducts.model.OrderDetails;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Override
    public OrderDetails  createOrder(MultipartFile file, List<String> items) {

        // validate file
        if (file.isEmpty()) {
            log.error("Data File is Empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data File is Empty");
        } else {

            // parse CSV file to create a list of `Order` objects
            try (
                    InputStreamReader inputStreamReader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
                    Reader reader = new BufferedReader(inputStreamReader);
            ) {
                // create csv bean reader
                CsvToBean<Order> csvToBean = new CsvToBeanBuilder(reader)
                        .withType(Order.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                // convert `CsvToBean` object to list of order
                List<Order> orders = csvToBean.parse();

                Map<String, Float> productPriceMap = new HashMap<>();
                for (Order o : orders) {
                    for (String p : o.getLabel()) {
                        String product = p + "_" + o.getShopId();
                        productPriceMap.put(product, o.getPrice());
                    }
                }

                Map<Integer, Float> shopPriceMap = new HashMap<>();

                Map<String, Boolean> productExistMap = items.stream()
                        .collect(Collectors.toMap(e -> e, e -> false));

                for (String item : items) {
                    for (Map.Entry<String, Float> entry : productPriceMap.entrySet()) {
                        if (entry.getKey().contains(item)) {

                            if (productExistMap.containsKey(item)) {
                                productExistMap.put(item, true);
                            }
                            String[] part = entry.getKey().split("(?<=\\D)(?=\\d)");
                            int shopId = Integer.parseInt(part[1]);
                            if (shopPriceMap.containsKey(shopId)) {
                                shopPriceMap.put(shopId, shopPriceMap.get(shopId) + entry.getValue());
                            } else {
                                shopPriceMap.put(shopId, entry.getValue());
                            }
                        }
                    }
                }

                if (shopPriceMap.isEmpty() || productExistMap.containsValue(false)) {
                    log.error("Required Products doesnt exist in Store");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required Products doesnt exist in Store");
                }

                Optional<Map.Entry<Integer, Float>> minPriceOptional = shopPriceMap.entrySet()
                        .stream()
                        .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                        .findFirst();

                OrderDetails orderDetails = new OrderDetails();
                if(minPriceOptional.isPresent()) {
                    Map.Entry<Integer, Float > minPrice = minPriceOptional.get();

                    orderDetails.setPrice(minPrice.getValue());
                    orderDetails.setShopId(minPrice.getKey());

                }
                return orderDetails;

            } catch (Exception e) {
                log.error("Server encountered exception :{} ", e.getStackTrace());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server encountered error");
            }
        }
    }
}


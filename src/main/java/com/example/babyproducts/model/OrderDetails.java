package com.example.babyproducts.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OrderDetails {

    private Integer shopId;
    private Float price;
}

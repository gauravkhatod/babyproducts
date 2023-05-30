package com.example.babyproducts.model;

import com.opencsv.bean.*;
import lombok.*;

import java.util.List;


@Data
@Getter
@Setter
public class Order {

    @CsvBindByName(column = "shopId")
    private Integer shopId;
    @CsvBindByName()
    private Float price;
    @CsvBindAndSplitByName(column ="label", elementType = String.class, splitOn = ",")
    private List<String> label;
}

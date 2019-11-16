package org.nico.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {

    private Long id;

    private Float price;

    private String productCode;

    private String productName;

    private String Description;

}



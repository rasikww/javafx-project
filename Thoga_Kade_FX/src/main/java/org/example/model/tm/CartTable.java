package org.example.model.tm;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartTable {
    private String itemCode;
    private String desc;
    private Double unitPrice;
    private Integer qty;
    private Double total;
}

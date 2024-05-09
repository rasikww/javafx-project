package org.example.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class OrderDetail {
    private String OrderId;
    private String itemCode;
    private Integer qty;
    private Double discount;
}

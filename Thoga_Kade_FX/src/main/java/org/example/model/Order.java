package org.example.model;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Order {
    private String orderId;
    private LocalDate orderDate;
    private String custId;
    private List<OrderDetail> orderDetailList;
}

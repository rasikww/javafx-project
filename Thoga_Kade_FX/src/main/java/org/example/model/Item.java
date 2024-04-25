package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {
   private String itemCode;
   private String desc;
   private String packSize;
   private Double unitPrice;
   private Integer qtyOnHand;

   public String toStringIdName() {
      return this.getItemCode()+" - "+this.getDesc();
   }
}

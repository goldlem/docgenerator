package by.urbel.docgenerator.entity;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Product implements Mappable {
    private int id;
    private String name;
    private String description;
    private Double quantity;
    private Double price;
    private Double discount;
    private Double tax;

    public Double getTotal() {
        if (quantity != null && price != null) {
            return quantity * price;
        } else {
            return 0.0;
        }
    }

    public Map<String, String> mapping() {
        String formattedName = name == null ? "" : name;
        String formattedDescription = description == null ? "" : description;
        String formattedNameDescription = name == null ? "" : name + ": " + description.toLowerCase();
        String formattedQuantity = quantity == null ? "" : String.format("%.2f", quantity);
        String formattedPrice = price == null ? "" : String.format("$%.2f", price);
        String formattedDiscount = discount == null ? "" : String.format("$%.2f", discount);
        String formattedTax = tax == null ? "" : "x";
        String formattedTotal = getTotal().equals(0.0) ? "" : String.format("$%.2f", getTotal());

        Map<String, String> map = new HashMap<>();
        map.put(String.format("[Product id %d]", id), String.valueOf(id));
        map.put(String.format("[Product name %d]", id), formattedName);
        map.put(String.format("[Product description %d]", id), formattedDescription);
        map.put(String.format("[Product name description %d]", id), formattedNameDescription);
        map.put(String.format("[Quantity %d]", id), formattedQuantity);
        map.put(String.format("[Price %d]", id), formattedPrice);
        map.put(String.format("[Product discount %d]", id), formattedDiscount);
        map.put(String.format("[Product tax %d]", id), formattedTax);
        map.put(String.format("[Product total %d]", id), formattedTotal);
        return map;
    }
}

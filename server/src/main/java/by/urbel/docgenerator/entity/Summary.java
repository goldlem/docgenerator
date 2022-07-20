package by.urbel.docgenerator.entity;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
public class Summary implements Mappable {
    private List<Product> products;
    private double discountRate;
    private double taxRate;

    public double getSubtotal() {
        return products.stream()
                .map(Product::getTotal)
                .reduce(Double::sum)
                .get();
    }

    public double getTotalDiscount() {
        return getSubtotal() * discountRate / 100;
    }

    public double getTax() {
        return products.stream()
                .map(Product::getTax)
                .filter(Objects::nonNull)
                .reduce(Double::sum)
                .get();
    }

    public double getTotal() {
        double total = getSubtotal();
        return total - getTotalDiscount();
    }

    @Override
    public Map<String, String> mapping() {
        Map<String, String> map = new HashMap<>();
        products.forEach(product -> map.putAll(product.mapping()));
        map.put("[Subtotal]", String.format("%.2f", getSubtotal()));
        map.put("[Tax]", String.format("%.2f", getTax()));
        map.put("[Tax rate]", String.format("%.2f", taxRate));
        map.put("[Total discount]", String.format("%.2f", getTotalDiscount()));
        map.put("[Discount rate]", String.format("%.2f", discountRate));
        map.put("[Total]", String.format("%.2f", getTotal()));
        return map;
    }
}

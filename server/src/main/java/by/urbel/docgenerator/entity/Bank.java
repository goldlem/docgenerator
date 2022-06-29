package by.urbel.docgenerator.entity;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Bank implements Mappable {
    private String name;
    private String account;
    private String routingNumber;

    @Override
    public Map<String, String> mapping() {
        return Map.of(
                "[Bank name]", name,
                "[Bank account]", account,
                "[Routing number]", routingNumber
        );
    }
}

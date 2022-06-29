package by.urbel.docgenerator.data;

import by.urbel.docgenerator.entity.Mappable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class InvRemData implements Mappable {
    InvoiceData invoice;
    RemittanceData remittance;

    @Override
    public Map<String, String> mapping() {
        Map<String, String> map = new HashMap<>();
        map.putAll(remittance.mapping());
        map.putAll(invoice.mapping());
        return map;
    }
}

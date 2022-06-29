package by.urbel.docgenerator.data;

import by.urbel.docgenerator.entity.Summary;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Data
@SuperBuilder
public class InvoiceData extends FinancialData {
    public static final String DISCOUNT_KEYWORD = "[Total discount]";
    private String purchaseOrder;
    private Summary summary;

    public Map<String, String> mapping() {
        Map<String, String> map = new HashMap<>(super.mapping());
        map.put("[Purchase order]", purchaseOrder);
        map.putAll(summary.mapping());
        return map;
    }
}

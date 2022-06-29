package by.urbel.docgenerator.data;

import by.urbel.docgenerator.entity.Summary;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@SuperBuilder
public class RemittanceData extends FinancialData {
    private String remittanceNumber;
    private String paymentNumber;
    private String customerId;
    private String receiptNumber;
    private LocalDate invoiceDate;
    private LocalDate paymentDate;
    private Double paid;
    private Double owing;
    private Summary summary;

    @Override
    public Map<String, String> mapping() {
        Map<String, String> map = new HashMap<>(super.mapping());
        map.put("[Remittance number]", remittanceNumber);
        map.put("[Payment number]", paymentNumber);
        map.put("[Receipt number]", paymentNumber);
        map.put("[Customer id]", customerId);
        map.put("[Invoice date]", formatDate(invoiceDate));
        map.put("[Payment date]", formatDate(paymentDate));
        map.put("[Paid]", String.format("%.2f", paid));
        map.put("[Owing]", String.format("%.2f", owing));
        map.putAll(summary.mapping());
        return map;
    }
}

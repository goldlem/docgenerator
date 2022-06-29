package by.urbel.docgenerator.data;

import by.urbel.docgenerator.entity.Bank;
import by.urbel.docgenerator.entity.Company;
import by.urbel.docgenerator.entity.Mappable;
import by.urbel.docgenerator.entity.Person;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
@SuperBuilder
@Slf4j
public abstract class FinancialData implements Mappable {
    private String dateFormat;
    private String invoiceNumber;
    private LocalDate date;
    private LocalDate dueDate;
    private Company companyFrom;
    private Company companyTo;
    private Person contactPerson;
    private Bank bank;

    public String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return formatter.format(date);
    }

    public Map<String, String> mapping() {
        companyFrom.setDestination("from");
        companyTo.setDestination("to");

        Map<String, String> map = new HashMap<>();
        map.put("[Date]", formatDate(date));
        map.put("[Due date]", formatDate(dueDate));
        map.put("[Invoice number]", invoiceNumber);
        map.putAll(companyFrom.mapping());
        map.putAll(companyTo.mapping());
        map.putAll(contactPerson.mapping());
        map.putAll(bank.mapping());
        return map;
    }
}

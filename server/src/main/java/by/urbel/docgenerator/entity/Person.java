package by.urbel.docgenerator.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.util.Map;

@Data
public class Person implements Mappable {
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String email;
    @CsvBindByName
    private String phone;

    @Override
    public Map<String, String> mapping() {
        return Map.of(
                "[Person name]", name,
                "[Person email]", email,
                "[Person phone]", phone
        );
    }
}

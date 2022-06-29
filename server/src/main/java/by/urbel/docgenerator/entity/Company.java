package by.urbel.docgenerator.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company implements Mappable {
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String street;
    @CsvBindByName
    private String city;
    @CsvBindByName
    private String zipCode;
    @CsvBindByName
    private String phone;
    @CsvBindByName
    private String email;
    @CsvBindByName
    private String website;
    @CsvBindByName
    private String fax;

    private String destination;

    @Override
    public Map<String, String> mapping() {
        Map<String, String> map = new HashMap<>();
        map.put(String.format("[Company name %s]", destination), name);
        map.put(String.format("[Street %s]", destination), street);
        map.put(String.format("[City %s]", destination), city);
        map.put(String.format("[Zip code %s]", destination), zipCode);
        map.put(String.format("[Phone %s]", destination), phone);
        map.put(String.format("[Email %s]", destination), email);
        map.put(String.format("[Website %s]", destination), website);
        map.put(String.format("[Fax %s]", destination), fax);
        return map;
    }
}
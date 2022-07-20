package by.urbel.docgenerator.entity;

import lombok.Data;

import javax.validation.constraints.Min;
import java.util.List;

@Data
public class RequestHtmlGenerationParams {
    private List<String> template;
    @Min(value = 1,message = "Min documents number is 1!")
    private int documentNumber;
}

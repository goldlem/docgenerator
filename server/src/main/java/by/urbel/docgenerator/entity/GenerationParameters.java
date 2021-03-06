package by.urbel.docgenerator.entity;

import lombok.Data;

import javax.validation.constraints.Min;
import java.util.List;

@Data
public class GenerationParameters {
    private List<String> invoiceType;
    private List<String> remittanceType;
    private List<String> fileExtension;
    @Min(value = 1,message = "Min documents number is 1!")
    private int docNumber;
}

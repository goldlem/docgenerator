package by.urbel.docgenerator.entity;

import lombok.Data;

import java.util.List;

@Data
public class GenerationParameters {
    private List<String> invoiceType;
    private List<String> remittanceType;
    private List<String> fileExtension;
    private int docNumber;
}

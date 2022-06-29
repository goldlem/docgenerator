package by.urbel.docgenerator.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class GenerationParameters {
    @NotBlank(message = "The invoiceTemplate is empty!")
    private String invoiceTemplate;
    private String remittanceTemplate;
    @Positive(message = "The number of documents to generate cannot be less than 1!")
    private int documentNumber;
    @NotBlank(message = "The extension of generated file is empty!")
    private String fileExtension;
}

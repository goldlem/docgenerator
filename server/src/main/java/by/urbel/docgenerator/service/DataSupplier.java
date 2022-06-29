package by.urbel.docgenerator.service;

import by.urbel.docgenerator.data.InvRemData;
import by.urbel.docgenerator.data.InvoiceData;
import by.urbel.docgenerator.data.RemittanceData;
import by.urbel.docgenerator.entity.*;
import by.urbel.docgenerator.template.DocumentType;
import by.urbel.docgenerator.util.Constants;
import by.urbel.docgenerator.util.FileUtils;
import by.urbel.docgenerator.util.Randomizer;
import by.urbel.docgenerator.util.Utils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DataSupplier {
    private final List<Company> companies;
    private final List<String> cities;
    private final List<String> productNames;
    private final List<String> productDescriptions;
    private final List<Person> persons;

    private final String datePrefix = "-d";
    private final String companyPrefix = "-c";
    private final List<Integer> discountRates = List.of(5, 10, 15);
    private final List<Integer> taxRates = List.of(1, 7, 10, 20);
    private final List<String> dateFormats = List.of(
            "dd/MM/YYYY",
            "YYYY-MM-dd",
            "dd MMM, YYYY"
    );
    private final List<Company> preparedCompanies = getPreparedCompanies();

    public DataSupplier() {
        companies = FileUtils.csvToBeans(Constants.COMPANY_DATA_PATH, Company.class).stream()
                .filter(company -> !company.getZipCode().isBlank())
                .collect(Collectors.toList());
        persons = FileUtils.csvToBeans(Constants.PERSON_DATA_PATH, Person.class);
        productNames = FileUtils.readLines(Constants.PRODUCT_NAME_PATH);
        productDescriptions = FileUtils.readLines(Constants.PRODUCT_DESC_PATH);
        cities = FileUtils.csvToMap(Constants.CITIES_PATH).get("city_ascii");
    }

    public Mappable getTemplateData(Path templatePath) {
        DocumentType type = Utils.extractDocumentType(templatePath);
        if (type.equals(DocumentType.INVOICE)) {
            return getInvoiceData(templatePath);
        } else if (type.equals(DocumentType.REMITTANCE)) {
            return getRemittanceData(templatePath);
        } else if (type.equals(DocumentType.INV_WITH_REM)) {
            return getInvRemData(templatePath);
        } else {
            throw new RuntimeException("Unsupported docType");
        }
    }

    private InvoiceData getInvoiceData(Path templatePath) {
        LocalDate date = Randomizer.getDate();
        String dateFormat = getPreparedValue(templatePath, datePrefix, dateFormats);
        Company companyFrom = getPreparedValue(templatePath, companyPrefix, preparedCompanies);
        Company companyTo = Randomizer.getItem(companies);
        companyTo.setCity(Randomizer.getItem(cities)
                .replace("\"", "")
                .replace("'", ""));
        companyTo.setName(companyTo.getName()
                .replace("\"", "")
                .replace("'", ""));

        return InvoiceData.builder()
                .date(date)
                .dueDate(Randomizer.getDate(date, 2, 0))
                .dateFormat(dateFormat)
                .invoiceNumber(Randomizer.getStrNumber(10))
                .purchaseOrder(Randomizer.getStrNumber(10))
                .contactPerson(Randomizer.getItem(persons))
                .summary(getSummary())
                .companyFrom(companyFrom)
                .companyTo(companyTo)
                .bank(getBank())
                .build();
    }

    private RemittanceData getRemittanceData(Path templatePath) {
        String dateFormat = getPreparedValue(templatePath, datePrefix, dateFormats);

        return RemittanceData.builder()
                .contactPerson(Randomizer.getItem(persons))
                .paymentNumber(Randomizer.getStrNumber(10))
                .remittanceNumber(Randomizer.getStrNumber(10))
                .receiptNumber(Randomizer.getStrNumber(10))
                .customerId(Randomizer.getStrNumber(10))
                .dateFormat(dateFormat)
                .bank(getBank())
                .build();
    }

    private InvRemData getInvRemData(Path templatePath) {
        InvoiceData invoiceData = getInvoiceData(templatePath);
        RemittanceData remittanceData = getRemittanceData(templatePath);
        DataMapper.mapTemplateData(invoiceData, remittanceData);
        return new InvRemData(invoiceData, remittanceData);
    }

    private Bank getBank() {
        return Bank.builder()
                .name(Randomizer.getItem(companies).getName())
                .account(Randomizer.getStrNumber(14))
                .routingNumber(Randomizer.getStrNumber(10))
                .build();
    }

    private Summary getSummary() {
        int productsNumber = Randomizer.getInt(1, 5);
        double discountRate = Randomizer.getItem(discountRates);
        return Summary.builder()
                .discountRate(discountRate)
                .taxRate(Randomizer.getItem(taxRates))
                .products(getProducts(productsNumber, discountRate))
                .build();
    }

    private List<Product> getProducts(int number, double discountRate) {
        List<Product> templateProducts = new ArrayList<>();
        for (int i = 1; i <= number; i++) {
            double price = Randomizer.getInt(10, 200);
            double quantity = Randomizer.getInt(1, 20);

            StringBuilder description = new StringBuilder();
            int descLength = Randomizer.getInt(2, 5);
            for (int j = 0; j < descLength; j++) {
                description.append(Randomizer.getItem(productDescriptions)).append(' ');
            }

            String productName = Randomizer.getItem(productNames)
                    .replace("\"", "")
                    .replace("'", "")
                    .split("-")[0].stripTrailing();

            Product product = Product.builder()
                    .id(i)
                    .name(Utils.formatSentence(productName))
                    .description(Utils.formatSentence(description.toString()))
                    .quantity(quantity)
                    .price(price)
                    .discount(0.0)
                    .tax(0.0)
                    .build();
            templateProducts.add(product);
        }
        for (int i = number + 1; i <= 10; i++) {
            Product product = Product.builder()
                    .id(i)
                    .build();
            templateProducts.add(product);
        }
        return templateProducts;
    }

    private <T> T getPreparedValue(Path templatePath, String patternPrefix, List<T> values) {
        int index = Utils.extractIndex(templatePath, patternPrefix);
        if (index == -1) {
            return Randomizer.getItem(values);
        } else {
            return values.get(index);
        }
    }

    private List<Company> getPreparedCompanies() {
        return List.of(
                Company.builder()
                        .name("Park City Group, Inc.")
                        .city("New York")
                        .street("4116 Barnett Park")
                        .zipCode("403964")
                        .website("https://parkcity.com")
                        .email("parkcity@pcg.com")
                        .phone("+255 (370) 773-7328")
                        .fax("+255 (370) 773-7300")
                        .build(),
                Company.builder()
                        .name("Foundation Medicine, Inc.")
                        .city("Chicago")
                        .street("9 Birchwood Alley")
                        .zipCode("964785")
                        .website("https://medicineinc.com")
                        .email("fmedicine@gmail.com")
                        .phone("+62 (865) 917-9478")
                        .fax("+62 (865) 917-9400")
                        .build(),
                Company.builder()
                        .name("Citizens & Northern Corp")
                        .city("Washington")
                        .street("087 Jackson Drive")
                        .zipCode("86-723")
                        .website("https://citizens.com")
                        .email("citizens@corp.com")
                        .phone("+86 (824) 519-7851")
                        .fax("+86 (824) 519-7800")
                        .build(),
                Company.builder()
                        .name("Allstate Corporation")
                        .city("Boston")
                        .street("03093 Tennessee Hill")
                        .zipCode("38-512")
                        .website("https://allstate.com")
                        .email("allstate@corp.com")
                        .phone("+7 (752) 199-8334")
                        .fax("+7 (752) 199-8300")
                        .build()
        );
    }
}

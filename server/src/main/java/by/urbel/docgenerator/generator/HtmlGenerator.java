package by.urbel.docgenerator.generator;

import by.urbel.docgenerator.entity.RequestHtmlGenerationParams;
import by.urbel.docgenerator.exception.ApiRequestException;
import by.urbel.docgenerator.service.DataSupplier;
import by.urbel.docgenerator.util.Constants;
import by.urbel.docgenerator.util.FileUtils;
import by.urbel.docgenerator.util.Randomizer;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HtmlGenerator {
    private final DataSupplier dataSupplier;
    private Document document;
    private Map<String, String> data;
    private final List<String> countries;
    private final List<String> countryDomainList;

    private static final String FILE_EXTENSION = ".html";
    private static final String DECIMAL_FORMAT = "#0.00";

    public HtmlGenerator() {
        dataSupplier = new DataSupplier();
        this.countries = FileUtils.csvToMap(Constants.CITIES_PATH).get("country");
        this.countryDomainList = FileUtils.csvToMap(Constants.CITIES_PATH).get("iso2");
    }

    public File generateHtml(RequestHtmlGenerationParams params) {
        List<File> files = new ArrayList<>();
        FileUtils.prepareOutputDir(Constants.OUT_HTML_PATH);
        try {
            for (int i = 0; i < params.getDocumentNumber(); ++i) {
                String templateName = Randomizer.getItem(params.getTemplate());
                document = FileUtils.readDocumentByName(templateName + FILE_EXTENSION);
                replaceData();
                String fileName = templateName + Randomizer.getStrNumber(5) + FILE_EXTENSION;
                File file = new File(Constants.OUT_HTML_PATH + File.separator + fileName);
                try (OutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(document.toString().getBytes());
                    files.add(file);
                }
            }
            return FileUtils.saveDocuments(files, Constants.OUT_PATH);
        } catch (IOException e) {
            throw new ApiRequestException("Exception message: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void replaceData() {
        data = dataSupplier.getInvoiceData().mapping();

        Element countryToElem = document.getElementById("country-to");
        if (countryToElem != null) {
            countryToElem.text(Randomizer.getItem(countries));
        }

        Element firstnameElem = document.getElementById("firstname-to");
        Element lastnameElem = document.getElementById("lastname-to");
        String[] fullName = data.get("[Person name]").split(" ");
        if (firstnameElem != null) {
            firstnameElem.text(fullName[0]);
        }
        if (lastnameElem != null) {
            lastnameElem.text(fullName[1]);
        }
        Element dobElem = document.getElementById("birth-date");
        if (dobElem != null) {
            dobElem.text(Randomizer.getDate(LocalDate.now().getYear() - 100).toString());
        }

        String vatNumber = Randomizer.getItem(countryDomainList) + Randomizer.getStrNumber(Randomizer.getInt(4, 15));
        Element vatNumberElem = document.getElementById("vat-number");
        if (vatNumberElem != null) {
            vatNumberElem.text(vatNumber);
        }

        String swiftNumber = Randomizer.getItem(countryDomainList) + Randomizer.getStrNumber(Randomizer.getInt(4, 15));
        Element swiftNumberElem = document.getElementById("swift-code");
        if (swiftNumberElem != null) {
            swiftNumberElem.text(swiftNumber);
        }

        replaceProductRows(document.getElementsByClass("product-row"));
        replacePrices();

        replaceTagValueById("full-name-to", "[Person name]");
        replaceTagValueById("phone-to", "[Person phone]");
        replaceTagValueById("invoice-number", "[Invoice number]");
        replaceTagValueById("invoice-date", "[Date]");
        replaceTagValueById("due-date", "[Due date]");
        replaceTagValueById("bank-name", "[Bank name]");
        replaceTagValueById("bank-account", "[Bank account]");
        replaceTagValueById("company-name-from", "[Company name from]");
        replaceTagValueById("street-from", "[Street from]");
        replaceTagValueById("city-from", "[City from]");
        replaceTagValueById("email-from", "[Email from]");
        replaceTagValueById("phone-from", "[Phone from]");
        replaceTagValueById("fax-from", "[Fax from]");
        replaceTagValueById("zip-code-from", "[Zip code from]");
        replaceTagValueById("street-to", "[Street to]");
        replaceTagValueById("city-to", "[City to]");
        replaceTagValueById("zip-code-to", "[Zip code to]");
        replaceTagValueById("email-to", "[Person email]");
        replaceTagValueById("subtotal", "[Subtotal]");

        replaceTagValueById("tax", "[Tax]");
        replaceTagValueById("tax-rate", "[Tax rate]");
        replaceTagValueById("total-discount", "[Total discount]");
        replaceTagValueById("total-discount-rate", "[Discount rate]");
    }

    private boolean replaceTagValueById(String tagId, String mapKey) {
        Element element = document.getElementById(tagId);
        String newValue = data.get(mapKey);

        if (element != null) {
            element.text(newValue);
            if (tagId.startsWith("email") && element.hasAttr("href")) {
                element.attr("href", String.format("mailto:%s", newValue));
            }
            return true;
        }
        return false;
    }

    private void replacePrices() {
//        if (replaceTagValueById("tax", "[Tax]") || replaceTagValueById("tax-rate", "[Tax rate]")) {
//            if (!replaceTagValueById("total-discount", "[Total discount]")
//                    && !replaceTagValueById("total-discount-rate", "[Discount rate]")) {
//                Double subTotalPrice = Double.parseDouble(data.get("[Subtotal]").replace(',', '.'));
//                Double tax = Double.parseDouble(data.get("[Tax]").replace(',', '.'));
//
//                String price = new DecimalFormat(DECIMAL_FORMAT).format(subTotalPrice + tax);
//                Objects.requireNonNull(document.getElementById("total-price")).text(price);
//            } else {
//                replaceTagValueById("total-discount-rate", "[Discount rate]");
//                replaceTagValueById("total-price", "[Total]");
//            }
//        } else {
            if (replaceTagValueById("total-discount", "[Total discount]")
                    || replaceTagValueById("total-discount-rate", "[Discount rate]")) {
                double discountRate = Double.parseDouble(data.get("[Discount rate]").replace(',', '.'));
                double subtotal = Double.parseDouble(data.get("[Subtotal]").replace(',', '.'));
                String totalPrice = new DecimalFormat(DECIMAL_FORMAT).format(subtotal - subtotal * discountRate / 100);
                Objects.requireNonNull(document.getElementById("total-price")).text(totalPrice);
            } else {
                replaceTagValueById("total-price", "[Subtotal]");
            }
//        }
    }

    private void replaceProductRows(Elements productRows) {
        List<String> idList = data.keySet().stream().sorted().filter(elem -> elem.startsWith("[Product id"))
                .map(data::get).collect(Collectors.toList());
        Element element = productRows.first();
        if (element != null) {
            Element parent = element.parent();
            if (parent != null) {
                parent.empty();
                for (String id : idList) {
                    String productName = data.get(String.format("[Product name %s]", id));
                    if (!productName.isBlank()) {
                        addProductRow(parent, element, id);
                    }
                }
            }
        }
    }

    private void addProductRow(Element parent, Element template, String productId) {
        replaceFirstTagValueByClass("product-id", String.format("[Product id %s]", productId), template);
        replaceFirstTagValueByClass("product", String.format("[Product name %s]", productId), template);
        replaceFirstTagValueByClass("product-name", String.format("[Product name %s]", productId), template);
        replaceFirstTagValueByClass("product-description", String.format("[Product description %s]", productId), template);
        replaceFirstTagValueByClass("product-price", String.format("[Price %s]", productId), template);
        replaceFirstTagValueByClass("product-quantity", String.format("[Quantity %s]", productId), template);
        replaceFirstTagValueByClass("product-tax", String.format("[Product tax value %s]", productId), template);
        replaceFirstTagValueByClass("product-total", String.format("[Product total %s]", productId), template);
        parent.append(template.html());
    }

    private void replaceFirstTagValueByClass(String tagClass, String mapKey, Element parent) {
        String value = data.get(mapKey);
        if (parent == null) {
            parent = document;
        }
        Element element = parent.getElementsByClass(tagClass).first();
        if (element != null) {
            if (tagClass.equals("product")) {
                element.append("p").attr("class", "product-description");
            }
            element.text(value);
        }
    }
}

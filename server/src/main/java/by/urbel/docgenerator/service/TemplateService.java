package by.urbel.docgenerator.service;

import by.urbel.docgenerator.data.FinancialData;
import by.urbel.docgenerator.data.InvRemData;
import by.urbel.docgenerator.data.InvoiceData;
import by.urbel.docgenerator.entity.Mappable;
import by.urbel.docgenerator.entity.Product;
import by.urbel.docgenerator.template.DocumentType;
import by.urbel.docgenerator.template.Template;
import by.urbel.docgenerator.template.engine.TemplateEngine;
import by.urbel.docgenerator.util.FileUtils;
import by.urbel.docgenerator.util.Randomizer;
import by.urbel.docgenerator.util.Utils;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Slf4j
public class TemplateService {
    private final TemplateEngine engine;
    private final DataSupplier dataSupplier;

    public TemplateService() {
        engine = new TemplateEngine();
        dataSupplier = new DataSupplier();
    }

    public Template createTemplate(Path path) {
        DocumentType type = Utils.extractDocumentType(path);
        try (InputStream stream = FileUtils.getResourceAsIOStream(path.toString())) {
            XWPFDocument document = new XWPFDocument(stream);
            Mappable data = dataSupplier.getTemplateData(path);
            return new Template(path, type, data, document);
        } catch (IOException e) {
            log.warn(String.format("Failed to load template with path: '%s', cause: %s",
                    path, e.getCause()));
            throw new RuntimeException(e);
        }
    }

    public Template processTemplate(Template template) {
        prepareTemplateData(template);
        return engine.process(template);
    }

    private void prepareTemplateData(Template template) {
        DocumentType type = template.getDocumentType();
        String docText = engine.collectText(template.getDocument());

        if (type.equals(DocumentType.INVOICE)) {
            prepareInvoice((InvoiceData) template.getData(), docText);
        }
        if (type.equals(DocumentType.INV_WITH_REM)) {
            prepareInvRem((InvRemData) template.getData(), docText);
        }
    }

    private void prepareInvRem(InvRemData data, String docText) {
        prepareInvoice(data.getInvoice(), docText);
    }

    private void prepareInvoice(InvoiceData data, String docText) {
        List<Product> products = data.getSummary().getProducts();
        boolean productDiscountExists = Utils.isMatchFound("Product discount [0-9]+", docText);
        boolean totalDiscountExists = Utils.isMatchFound("Discount rate", docText);
        if (!productDiscountExists) {
            products.forEach(product -> product.setDiscount(0.0));
        }
        if (!totalDiscountExists) {
            data.getSummary().setDiscountRate(0);
        }
    }
}

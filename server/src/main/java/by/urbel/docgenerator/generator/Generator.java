package by.urbel.docgenerator.generator;

import by.urbel.docgenerator.entity.GenerationParameters;
import by.urbel.docgenerator.service.DataMapper;
import by.urbel.docgenerator.service.TemplateService;
import by.urbel.docgenerator.template.Template;
import by.urbel.docgenerator.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

import static by.urbel.docgenerator.util.Constants.OUT_PATH;
import static by.urbel.docgenerator.util.Constants.TEMPLATE_DIR;

@Component
@Slf4j
public class Generator {
    private final TemplateService templateService;
    private final List<Template> templates = new ArrayList<>();

    public Generator() {
        templateService = new TemplateService();
    }

    public File run(GenerationParameters generationParam) throws IOException, URISyntaxException {
        templates.clear();
        String invoiceType = generationParam.getInvoiceTemplate();
        String remittanceType = generationParam.getRemittanceTemplate();

        Path invoiceTemplatePath = FileUtils.findTemplatePath(TEMPLATE_DIR, invoiceType);
        for (int i = 0; i < generationParam.getDocumentNumber(); ++i) {
            Template invoiceTemplate = generateInvoice(invoiceTemplatePath);
            if (!remittanceType.isBlank() && invoiceType.toLowerCase().startsWith("invoice")) {
                Path remittanceTemplatePath = FileUtils.findTemplatePath(TEMPLATE_DIR, remittanceType);
                generateRemittance(invoiceTemplate, remittanceTemplatePath);
            }
        }
        log.info(String.format("Generated %s documents for each document type", generationParam.getDocumentNumber()));

        return FileUtils.saveDocuments(templates, OUT_PATH, generationParam.getFileExtension());
    }

    private Template generateInvoice(Path templatePath) {
        Template template = templateService.createTemplate(templatePath);
        template = templateService.processTemplate(template);
        templates.add(template);
        return template;
    }

    private void generateRemittance(Template invoice, Path templatePath) {
        Template remittance = templateService.createTemplate(templatePath);
        DataMapper.mapTemplateData(invoice.getData(), remittance.getData());
        remittance = templateService.processTemplate(remittance);
        templates.add(remittance);
    }
}


package by.urbel.docgenerator.generator;

import by.urbel.docgenerator.entity.GenerationParameters;
import by.urbel.docgenerator.service.DataMapper;
import by.urbel.docgenerator.service.TemplateService;
import by.urbel.docgenerator.template.DocumentType;
import by.urbel.docgenerator.template.Template;
import by.urbel.docgenerator.util.FileUtils;
import by.urbel.docgenerator.util.Randomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

import static by.urbel.docgenerator.util.Constants.OUT_PATH;
import static by.urbel.docgenerator.util.Constants.TEMPLATE_DIR;

@Component
@Slf4j
public class DocGenerator {
    private final TemplateService templateService;
    private final List<Template> templates = new ArrayList<>();

    public DocGenerator() {
        templateService = new TemplateService();
    }

    public File run(GenerationParameters generationParam) throws IOException, URISyntaxException {
        templates.clear();
        for (int i = 0; i < generationParam.getDocNumber(); ++i) {
            String invoiceType = Randomizer.getItem(generationParam.getInvoiceType());
            Path invoiceTemplatePath = FileUtils.findTemplatePath(TEMPLATE_DIR, invoiceType);
            Template invoiceTemplate = generateInvoice(invoiceTemplatePath);
            if (!invoiceTemplate.getDocumentType().equals(DocumentType.INV_WITH_REM)) {
                String remittanceType = Randomizer.getItem(generationParam.getRemittanceType());
                Path remittanceTemplatePath = FileUtils.findTemplatePath(TEMPLATE_DIR, remittanceType);
                generateRemittance(invoiceTemplate, remittanceTemplatePath);
            }
        }
        log.info(String.format("Generated %s documents", generationParam.getDocNumber()));

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


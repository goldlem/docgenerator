package by.urbel.docgenerator.controller;

import by.urbel.docgenerator.entity.RequestHtmlGenerationParams;
import by.urbel.docgenerator.exception.ApiRequestException;
import by.urbel.docgenerator.generator.DocGenerator;
import by.urbel.docgenerator.entity.GenerationParameters;
import by.urbel.docgenerator.generator.HtmlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
public class Controller {
    private final DocGenerator generator;
    private final HtmlGenerator htmlGenerator;

    @CrossOrigin()
    @PostMapping("/doc")
    public ResponseEntity<Resource> generateDoc(@Valid @RequestBody GenerationParameters params) {
        if (params.getInvoiceType().isEmpty()
                || params.getFileExtension().isEmpty()) {
            throw new ApiRequestException("InvoiceType and FileExtension cannot be empty", HttpStatus.BAD_REQUEST);
        }
        try {
            File zipFile = generator.run(params);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));
            return ResponseEntity.ok()
                    .contentLength(zipFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            throw new ApiRequestException(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @PostMapping("/html")
    public ResponseEntity<Resource> generateHtml(@Valid @RequestBody RequestHtmlGenerationParams params) {
        if (params.getTemplate().isEmpty()) {
            throw new ApiRequestException("Templates cannot be empty", HttpStatus.BAD_REQUEST);
        }
        try {
            File zipFile = htmlGenerator.generateHtml(params);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));
            return ResponseEntity.ok()
                    .contentLength(zipFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            throw new ApiRequestException(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package by.urbel.docgenerator.controller;

import by.urbel.docgenerator.exception.ApiRequestException;
import by.urbel.docgenerator.generator.Generator;
import by.urbel.docgenerator.entity.GenerationParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

@CrossOrigin
@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
public class Controller {
    private final Generator generator;

    @PostMapping()
    public ResponseEntity<Resource> generateDoc(@Valid @RequestBody GenerationParameters generationParameters) {
        try {
            File zipFile = generator.run(generationParameters);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));
            return ResponseEntity.ok()
                    .contentLength(zipFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

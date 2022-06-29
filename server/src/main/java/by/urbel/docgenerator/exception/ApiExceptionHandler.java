package by.urbel.docgenerator.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<String> handleNotFoundException(ApiRequestException e){
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }
}

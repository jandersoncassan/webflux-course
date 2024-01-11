package br.com.jande.webfluxcourse.controller.exceptions;

import br.com.jande.webfluxcourse.service.exception.ObjectNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<Mono<StandardError>> duplicatedKeyException(
            DuplicateKeyException ex, ServerHttpRequest request
    ){
        return ResponseEntity.badRequest()
                .body(Mono.just(StandardError.builder()
                                .timestamp(LocalDateTime.now())
                                .path(request.getPath().toString())
                                .status(BAD_REQUEST.value())
                                .error(BAD_REQUEST.getReasonPhrase())
                                .message(verifyMessageException(ex.getMessage()))
                                .build()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Mono<ValidationError>> validationError(
            WebExchangeBindException ex, ServerHttpRequest request
    ){
        ValidationError error = new ValidationError(
                LocalDateTime.now(),
                request.getPath().toString(),
                BAD_REQUEST.value(),
               "Validation Error",
               "Error validation attributes"
        );

        for(FieldError x: ex.getBindingResult().getFieldErrors()){
            error.addError(x.getField(), x.getDefaultMessage());
        }

        return ResponseEntity.badRequest()
                .body(Mono.just(error));
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    ResponseEntity<Mono<StandardError>> objectNotFoundException(
            ObjectNotFoundException ex, ServerHttpRequest request
    ){
        return ResponseEntity.status(NOT_FOUND)
                .body(Mono.just(StandardError.builder()
                        .timestamp(LocalDateTime.now())
                        .path(request.getPath().toString())
                        .status(NOT_FOUND.value())
                        .error(NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .build()));
    }


    private String verifyMessageException(final String message){
        if(message.contains("email dup key"))
            return "E-mail already registered";
        return "E-mail duplicate Key Exception";
    }
}

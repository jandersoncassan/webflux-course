package br.com.jande.webfluxcourse.controller.exceptions;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<Mono<StandardError>> duplicatedKeyException(
            DuplicateKeyException ex, ServerHttpRequest request
    ){
        return ResponseEntity.badRequest()
                .body(Mono.just(StandardError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(BAD_REQUEST.value())
                                .error(BAD_REQUEST.getReasonPhrase())
                                .message(verifyMessageException(ex.getMessage()))
                                .path(request.getPath().toString())

                        .build()));
    }

    private String verifyMessageException(final String message){
        if(message.contains("email dup key"))
            return "E-mail already registered";
        return "E-mail duplicate Key Exception";
    }
}
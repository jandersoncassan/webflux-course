package br.com.jande.webfluxcourse.controller.exceptions;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class StandardError implements Serializable {

    private static final long serialVersionUUID = 1L;

    private LocalDateTime timestamp;
    private String path;
    private Integer status;
    private String error;
    private String message;
}

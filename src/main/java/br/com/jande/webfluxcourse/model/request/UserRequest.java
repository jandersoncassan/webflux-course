package br.com.jande.webfluxcourse.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "must not be null or empty")
        @Size(min = 3, max = 50, message = "must be between 3 and 50 characters")
        String name,
        @NotBlank(message = "must not be null or empty")
        @Email(message = "invalid email")
        String email,
        @NotBlank(message = "must not be null or empty")
        @Size(min = 3, max = 50, message = "must be between 3 and 50 characters")
        String password
) {
}

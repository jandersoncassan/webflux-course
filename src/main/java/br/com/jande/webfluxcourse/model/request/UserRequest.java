package br.com.jande.webfluxcourse.model.request;

public record UserRequest(
        String name,
        String email,
        String password
) {
}

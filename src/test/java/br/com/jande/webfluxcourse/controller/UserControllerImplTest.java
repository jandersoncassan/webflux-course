package br.com.jande.webfluxcourse.controller;

import br.com.jande.webfluxcourse.entity.User;
import br.com.jande.webfluxcourse.mapper.UserMapper;
import br.com.jande.webfluxcourse.model.request.UserRequest;
import br.com.jande.webfluxcourse.model.response.UserResponse;
import br.com.jande.webfluxcourse.service.UserService;
import br.com.jande.webfluxcourse.service.exception.ObjectNotFoundException;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService service;

    @MockBean
    private MongoClient mongoClient;

    @MockBean
    private UserMapper mapper;

    @Test
    @DisplayName("Test endpoint save with success")
    void testSaveWithSuccess() {
        final var request = new UserRequest("Jande", "jande.max@teste.com.br", "123456");
        when(service.save(any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));

        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated();

        verify(service, times(1)).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("Test endpoint save with Bad Request")
    void testSaveWithNameBadRequest() {
        final var request = new UserRequest(" Jande", "jande.max@teste.com.br", "123456");

       webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
               .expectBody()
               .jsonPath("$.path").isEqualTo("/users")
               .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
               .jsonPath("$.error").isEqualTo("Validation Error")
               .jsonPath("$.message").isEqualTo("Error validation attributes")
               .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("field cannot have blank spaces at the beginning or at end");

    }

    @Test
    @DisplayName("Test endpoint findById with success")
    void testindByIdWithSuccess() {
        final var response = new UserResponse("123456789", "Jande", "jande.max@teste.com.br", "123456");

        when(service.findById(anyString())).thenReturn(Mono.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(response);

        webTestClient.get()
                .uri("/users/"+123456789)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123456789")
                .jsonPath("$.name").isEqualTo("Jande")
                .jsonPath("$.email").isEqualTo("jande.max@teste.com.br")
                .jsonPath("$.password").isEqualTo("123456");

        verify(service, times(1)).findById(anyString());

    }


    @Test
    @DisplayName("Test endpoint findAll with success")
    void findAll() {
        final var response = new UserResponse("123456789", "Jande", "jande.max@teste.com.br", "123456");

        when(service.findAll()).thenReturn(Flux.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(response);

        webTestClient.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo("123456789")
                .jsonPath("$.[0].name").isEqualTo("Jande")
                .jsonPath("$.[0].email").isEqualTo("jande.max@teste.com.br")
                .jsonPath("$.[0].password").isEqualTo("123456");

        verify(service, times(1)).findAll();
        verify(mapper).toResponse(any(User.class));

    }

    @Test
    @DisplayName("Test endpoint update with success")
    void update() {

        final var request = new UserRequest("Jandera", "jande.max@teste.com.br", "123456");
        final var response = new UserResponse("123456789", "Jande", "jande.max@teste.com.br", "123456");

        when(service.update(anyString(), any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(response);

        webTestClient.patch()
                .uri("/users/"+"123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123456789")
                .jsonPath("$.name").isEqualTo("Jande")
                .jsonPath("$.email").isEqualTo("jande.max@teste.com.br")
                .jsonPath("$.password").isEqualTo("123456");

        verify(service, times(1)).update(anyString(), any(UserRequest.class));
        verify(mapper).toResponse(any(User.class)); //default time() = 1
    }

    @Test
    @DisplayName("Test endpoint delete with success")
    void delete() {

        when(service.delete(anyString())).thenReturn(Mono.just(User.builder().build()));

        webTestClient.delete()
                .uri("/users/"+"123456789")
                .exchange()
                .expectStatus().isOk();

        verify(service, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("Test endpoint delete with not found")
    void testDeleteThrownsObjectNotFound() {

        when(service.delete(anyString())).thenThrow(new ObjectNotFoundException("user not found"));

        webTestClient.delete()
                .uri("/users/"+"123456789")
                .exchange()
                .expectStatus().isNotFound();

        verify(service, times(1)).delete(anyString());
    }
}
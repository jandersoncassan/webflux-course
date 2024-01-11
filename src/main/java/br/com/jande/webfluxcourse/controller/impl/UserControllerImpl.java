package br.com.jande.webfluxcourse.controller.impl;

import br.com.jande.webfluxcourse.controller.UserController;
import br.com.jande.webfluxcourse.entity.User;
import br.com.jande.webfluxcourse.mapper.UserMapper;
import br.com.jande.webfluxcourse.model.request.UserRequest;
import br.com.jande.webfluxcourse.model.response.UserResponse;
import br.com.jande.webfluxcourse.service.UserService;
import br.com.jande.webfluxcourse.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="/users")
public class UserControllerImpl implements UserController {

    private final UserService service;
    private final UserMapper mapper;
    @Override
    public ResponseEntity<Mono<Void>> save(UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(service.save(request).then());
    }

    @Override
    public ResponseEntity<Mono<UserResponse>> findById(String id) {

        return ResponseEntity.status(HttpStatus.OK)
                        .body(service.findById(id)
                                .switchIfEmpty(Mono.error(
                                        new ObjectNotFoundException(
                                                format("Object not found, Id: %s, Type: %s", id, User.class.getSimpleName())
                                        )
                                ))
                                .map(mapper::toResponse));
    }

    @Override
    public ResponseEntity<Flux<UserResponse>> findAll() {
        return null;
    }

    @Override
    public ResponseEntity<Mono<UserResponse>> update(String id, UserRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Mono<Void>> delete(String id) {
        return null;
    }
}

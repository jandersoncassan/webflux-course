package br.com.jande.webfluxcourse.service;

import br.com.jande.webfluxcourse.entity.User;
import br.com.jande.webfluxcourse.mapper.UserMapper;
import br.com.jande.webfluxcourse.model.request.UserRequest;
import br.com.jande.webfluxcourse.repository.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService service;

    @Test
    void testSave() {
        UserRequest request = new UserRequest("Valdir", "email@email.com.br", "1234");
        User entity = User.builder().build();

        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any(User.class))).thenReturn(Mono.just(User.builder().build()));

        Mono<User> result = service.save(request);
        StepVerifier.create(result)
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

        Mockito.verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void testFindById(){
        when(repository.findById(any())).thenReturn(Mono.just(User.builder()
                        .id("1234")
                .build()));

        Mono<User> result = service.findById("123");
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class
                && user.getId().equals("1234"))
                .expectComplete()
                .verify();

        Mockito.verify(repository, times(1)).findById(any());
    }

    @Test
    void testFindAll(){
        when(repository.findAll()).thenReturn(Flux.just(User.builder()
                .id("1234")
                .build()));

        Flux<User> result = service.findAll();
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class
                        && user.getId().equals("1234"))
                .expectComplete()
                .verify();

        Mockito.verify(repository, times(1)).findAll();
    }
}

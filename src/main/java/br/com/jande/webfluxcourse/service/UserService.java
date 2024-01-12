package br.com.jande.webfluxcourse.service;

import br.com.jande.webfluxcourse.entity.User;
import br.com.jande.webfluxcourse.mapper.UserMapper;
import br.com.jande.webfluxcourse.model.request.UserRequest;
import br.com.jande.webfluxcourse.repository.UserRepository;
import br.com.jande.webfluxcourse.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    public Mono<User> save(final UserRequest request){
        return repository.save(mapper.toEntity(request));
    }

    public Mono<User> findById(final String id){
        return repository.findById(id);
    }

    public Flux<User> findAll(){
        return repository.findAll();
    }

    public Mono<User> update(final String id, final UserRequest request){
        return findById(id)
                .switchIfEmpty(Mono.error(
                        new ObjectNotFoundException(
                                format("Object not found, Id: %s, Type: %s", id, User.class.getSimpleName())
                        )
                ))
                .map(entity -> mapper.toEntity(request, entity))
                .flatMap(repository::save);

    }

    public Mono<User> delete(final String id){
        return handlerNotFound(repository.findAndRemove(id), id);
    }

    private <T> Mono<T> handlerNotFound(Mono<T> mono, String id){
        return mono.switchIfEmpty(Mono.error(
                new ObjectNotFoundException(
                        format("Object not found, Id: %s, Type: %s", id, User.class.getSimpleName())
                ))
        );
    }

}

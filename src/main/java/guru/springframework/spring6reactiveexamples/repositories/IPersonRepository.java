package guru.springframework.spring6reactiveexamples.repositories;

import guru.springframework.spring6reactiveexamples.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// for now let's emulate a repo func
public interface IPersonRepository {
    Mono<Person> getById(Integer id);

    Flux<Person> findAll();
}

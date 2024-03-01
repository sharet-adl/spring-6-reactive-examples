package guru.springframework.spring6reactiveexamples.repositories;

import guru.springframework.spring6reactiveexamples.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class PersonRepository implements IPersonRepository {
    Person michael = Person.builder().id(1).firstName("Michael").lastName("P1").build();
    Person fiona = Person.builder().id(2).firstName("Fiona").lastName("P2").build();
    Person sam = Person.builder().id(3).firstName("Sam").lastName("P3").build();
    Person jesse = Person.builder().id(4).firstName("Jesse").lastName("P4").build();

    @Override
    public Mono<Person> getById(final Integer id) {
        return findAll().filter(person -> person.getId().equals(id)).next();
    }

    @Override
    public Flux<Person> findAll() {
        return Flux.just(michael, fiona, sam, jesse);
    }
}

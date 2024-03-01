package guru.springframework.spring6reactiveexamples.repositories;

import guru.springframework.spring6reactiveexamples.domain.Person;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonRepositoryTest {

    PersonRepository personRepository = new PersonRepository();

    // Test on how to do a blocking - to avoid. Want to use a subscriber instead ..
    @Test
    void testMonoByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);
        Person person = personMono.block();
        System.out.println(person.toString());
    }

    // Non-blocking using Subscriber pattern
    @Test
    void testGetByIdSubscribe() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    // In a map op, can transform the stream that is flowing through the mono. Map it to a new value (type=String), Mono will wrap it.
    // The backpressure comes from calling effectively .Subscribe(); UNLESS there some type of backpressure, nothing is going to happen !
    //    Mono would not run otherwise ..
    @Test
    void testMapOperation() {
        Mono<Person> personMono = personRepository.getById(1);

//        personMono.map(person -> {
//            return person.getFirstName();
//        }).subscribe(firstName -> {
//            System.out.println(firstName);
//        });

        // replace Lambda with method reference
        personMono.map(Person::getFirstName).subscribe(System.out::println);
    }

    // blocking, will get only 1st one
    @Test
    void testFluxBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();

        // block and wait for the first element to come
        Person person = personFlux.blockFirst();
        System.out.println(person.toString());
    }

    // Will be executed for every element in the flux.
    @Test
    void testFluxSubscriber() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFluxMap() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.map(Person::getFirstName).subscribe(System.out::println);
    }

    // using list .. fully valid ?!
    @Test
    void testFluxToList() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<List<Person>> listMono = personFlux.collectList();

        listMono.subscribe(list -> {
            list.forEach(person -> System.out.println(person.getFirstName()));
        });
    }

    @Test
    void testFilterOnName(){
        personRepository.findAll()
                .filter(person -> person.getFirstName().equals("Fiona"))
                .subscribe(person -> System.out.println(person.getFirstName() + person.getLastName()));
    }

    // NEXT will return a mono of the type
    @Test
    void testGetById() {
        Mono<Person> fionaMono = personRepository.findAll()
                .filter(person -> person.getFirstName().equals("Fiona"))
                .next();

        fionaMono.subscribe(person -> System.out.println(person.getFirstName()));
    }

    // next() would not create any error, as an empty Mono will be returned. Use single(), and get 2 errors
    // commenting out the subscribe() call, would take the backpressure off and nothing will get called ..
    @Test
    void testFindPersonByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();

        // non existing id
        final Integer id = 8;



        //Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next()
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).single()
                .doOnError(throwable -> {
                    System.out.println("Error occured in the mono");
                    System.out.println(throwable.toString());
                });

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        },
                throwable -> {
                    System.out.println("Error occured in the mono");
                    System.out.println(throwable.toString());
                });
    }

    @Test
    void testGetByIdFound() {
        Mono<Person> personMono = personRepository.getById(3);

        assertTrue(personMono.hasElement().block());
    }

    @Test
    void testGetByIdFoundStepVerifier() {
        Mono<Person> personMono = personRepository.getById(3);

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();

        // put backpressure
        personMono.subscribe(person -> {
            System.out.println(person.getFirstName());
        });
    }

    @Test
    void testGetByIdNotFound() {
        Mono<Person> personMono = personRepository.getById(6);

        assertFalse(personMono.hasElement().block());
    }

    @Test
    void testGetByIdNotFoundStepVerifier() {
        Mono<Person> personMono = personRepository.getById(6);

        StepVerifier.create(personMono).expectNextCount(0).verifyComplete();

        // put backpressure
        personMono.subscribe(person -> {
            System.out.println(person.getFirstName());
        });
    }

}























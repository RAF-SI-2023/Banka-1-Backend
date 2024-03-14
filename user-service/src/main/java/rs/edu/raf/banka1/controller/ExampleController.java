package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/example")
@Tag(name = "Example", description = "Example API")
//@SecurityRequirement() TODO
@CrossOrigin
@SecurityRequirement(name = "basicScheme")
public class ExampleController {

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Pozdrav od servisa", description = "Vraca poruku pozdrava od user-service-a")
    public ResponseEntity<String> helloFromService() {
        return ResponseEntity.ok("Hello from user-service!");
    }

    // A method that returns a JSON string with key "test" and value "this is user-service"
    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Test endpoint", description = "Vraca JSON objekat sa kljucem 'test' i vrednoscu 'this is user-service'")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("{\"test\": \"this is user-service\"}");
    }

    // A POST method that takes a string and returns that script in uppercase with json content type
    // JSON should have key "word" and value that uppercase word
    // Example: {"word": "hello"} -> {"word": "HELLO"}
    @PostMapping(value = "/uppercase", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Uppercase", description = "Vraca JSON objekat sa kljucem 'word' i vrednoscu koja je prosledjeni string u velikim slovima")
    public ResponseEntity<String> uppercase(@RequestBody String word) {
        return ResponseEntity.ok("{\"word\": \"" + word.toUpperCase() + "\"}");
    }

    // Napravi metodu koju vraca Hello world!

    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Hello world", description = "Vraca poruku 'Hello world!'")
    @PreAuthorize("hasAuthority('test_permission')")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello world!");
    }
}

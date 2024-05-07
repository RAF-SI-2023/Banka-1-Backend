package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
@Tag(name = "Example", description = "Example API")
//@SecurityRequirement() TODO
@CrossOrigin
@SecurityRequirement(name = "basicScheme")
public class ExampleController {

    private MarketService marketService;

    @Autowired
    public ExampleController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Pozdrav od servisa", description = "Vraca poruku pozdrava od market-service-a")
    public ResponseEntity<String> helloFromService() {
        return ResponseEntity.ok(String.valueOf(marketService.exampleMethod()));
    }
}

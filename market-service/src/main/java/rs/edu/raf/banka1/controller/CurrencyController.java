package rs.edu.raf.banka1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.model.exceptions.CurrencyNotFoundException;
import rs.edu.raf.banka1.services.CurrencyService;
import rs.edu.raf.banka1.services.InflationService;

@RestController
@CrossOrigin
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final InflationService inflationService;

    @Autowired
    public CurrencyController(
            CurrencyService currencyService, InflationService inflationService) {
        this.currencyService = currencyService;
        this.inflationService = inflationService;
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(this.currencyService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        try {
            return ResponseEntity.ok(this.currencyService.findById(id));
        } catch (CurrencyNotFoundException currencyNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/code/{code}")
    public ResponseEntity<?> findByCurrencyCode(@PathVariable(name = "code") String currencyCode) {
        try {
            return ResponseEntity.ok(this.currencyService.findCurrencyByCurrencyCode(currencyCode));
        } catch (CurrencyNotFoundException currencyNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/{id}/inflation")
    public ResponseEntity<?> findInflationByCurrencyId(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(this.inflationService.findAllByCurrencyId(id));
    }

    @GetMapping(value = "/{id}/inflation/{year}")
    public ResponseEntity<?> findInflationByCurrencyIdAndYear(
            @PathVariable(name = "id") Long id, @PathVariable(name = "year") Integer year) {
        return ResponseEntity.ok(this.inflationService.findByYear(id, year));
    }

}

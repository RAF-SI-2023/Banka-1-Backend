package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.entities.Inflation;
import rs.edu.raf.banka1.repositories.InflationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class InflationService {

    private final InflationRepository inflationRepository;

    @Autowired
    public InflationService(InflationRepository inflationRepository) {
        this.inflationRepository = inflationRepository;
    }

    public Optional<List<Inflation>> findAllByCurrencyId(Long currencyId) {
        return Optional.ofNullable(this.inflationRepository.findAllByCurrencyId(currencyId));
    }

    public Optional<List<Inflation>> findByYear(Long currencyId, Integer year) {
        return Optional.ofNullable(this.inflationRepository.findAllByCurrencyIdAndYear(currencyId, year));
    }
}

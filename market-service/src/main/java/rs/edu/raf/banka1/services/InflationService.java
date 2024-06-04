package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.InflationMapper;
import rs.edu.raf.banka1.model.dtos.InflationDto;
import rs.edu.raf.banka1.repositories.InflationRepository;

import java.util.List;

@Service
public class InflationService {

    private final InflationRepository inflationRepository;
    private final InflationMapper inflationMapper;

    @Autowired
    public InflationService(InflationRepository inflationRepository, InflationMapper inflationMapper) {
        this.inflationRepository = inflationRepository;
        this.inflationMapper = inflationMapper;
    }

    @Cacheable(value = "findAllByCurrencyId", key = "#currencyId")
    public List<InflationDto> findAllByCurrencyId(Long currencyId) {
        return inflationRepository.findAllByCurrencyId(currencyId).stream().map(inflationMapper::inflationToInflationDto).toList();
    }

    @Cacheable(value = "findAllByCurrencyIdAndYear", key = "#currencyId + '_' + #year")
    public List<InflationDto> findAllByCurrencyIdAndYear(Long currencyId, Integer year) {
        return inflationRepository.findAllByCurrencyIdAndYear(currencyId, year).stream().map(inflationMapper::inflationToInflationDto).toList();
    }
}

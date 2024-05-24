package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.ExchangeMapper;
import rs.edu.raf.banka1.model.dtos.ExchangeDto;
import rs.edu.raf.banka1.repositories.ExchangeRepository;

import java.util.List;

@Service
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeRepository exchangeRepository;
    private final ExchangeMapper exchangeMapper;

    @Autowired
    public ExchangeServiceImpl(
            ExchangeRepository exchangeRepository,
            ExchangeMapper exchangeMapper) {
        this.exchangeRepository = exchangeRepository;
        this.exchangeMapper = exchangeMapper;
    }

    @Override
    public List<ExchangeDto> getAllExchanges() {
        return exchangeRepository.findAll().stream().map(exchangeMapper::exchangeToExchangeDto).toList();
    }

    @Override
    public ExchangeDto getExchangeById(Long id) {
        return this.exchangeRepository.findById(id).map(exchangeMapper::exchangeToExchangeDto).orElse(null);
    }
}

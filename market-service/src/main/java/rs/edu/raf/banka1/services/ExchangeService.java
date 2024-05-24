package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.dtos.ExchangeDto;

import java.util.List;

public interface ExchangeService {
    List<ExchangeDto> getAllExchanges();
    ExchangeDto getExchangeById(Long id);
}

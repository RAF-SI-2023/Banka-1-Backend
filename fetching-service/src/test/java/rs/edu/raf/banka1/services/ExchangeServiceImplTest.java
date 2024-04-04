package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.edu.raf.banka1.mapper.ExchangeMapper;
import rs.edu.raf.banka1.model.dtos.ExchangeDto;
import rs.edu.raf.banka1.model.entities.Country;
import rs.edu.raf.banka1.model.entities.Exchange;
import rs.edu.raf.banka1.repositories.CountryRepository;
import rs.edu.raf.banka1.repositories.ExchangeRepository;
import rs.edu.raf.banka1.repositories.HolidayRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExchangeServiceImplTest {
    private ExchangeServiceImpl exchangeService;
    private ExchangeRepository exchangeRepository;
    private CountryRepository countryRepository;
    private HolidayRepository holidayRepository;

    @BeforeEach
    public void setUp() {
        countryRepository = mock(CountryRepository.class);
        exchangeRepository = mock(ExchangeRepository.class);
        holidayRepository = mock(HolidayRepository.class);
        ExchangeMapper exchangeMapper = new ExchangeMapper();

        exchangeService = new ExchangeServiceImpl(countryRepository, holidayRepository, exchangeRepository, exchangeMapper);
    }
}

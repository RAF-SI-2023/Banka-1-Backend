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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertNull;

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

        exchangeService = new ExchangeServiceImpl(exchangeRepository, exchangeMapper);
    }

    @Test
    public void getAllExchangesTest() {
        List<Exchange> exchangeList = new ArrayList<>();
        exchangeList.add(new Exchange(1L, "test1", "test1", "test1", new Country(), null));
        exchangeList.add(new Exchange(2L, "test2", "test2", "test2", new Country(), null));
        when(exchangeRepository.findAll()).thenReturn(exchangeList);

        List<ExchangeDto> res = exchangeService.getAllExchanges();

        assertEquals(exchangeList.size(), res.size());
    }

    @Test
    public void getAllExchangesEmptyListTest() {
        List<Exchange> exchangeList = new ArrayList<>();

        when(exchangeRepository.findAll()).thenReturn(exchangeList);

        List<ExchangeDto> res = exchangeService.getAllExchanges();
        assertEquals(exchangeList.size(), res.size());
    }

    @Test
    public void getExchangeByIdTest() {
        long id = 1L;
        Exchange exchange = new Exchange(id, "test1", "test1", "test1", new Country(), null);
        when(exchangeRepository.findById(id)).thenReturn(Optional.of(exchange));

        ExchangeDto res = exchangeService.getExchangeById(id);

        assertEquals(exchange.getExchangeName(), res.getExchangeName());
    }

    @Test
    public void getExchangeByNonExistentIdTest() {
        long id = 1L;
        Exchange exchange = new Exchange(id, "test1", "test1", "test1", new Country(), null);
        when(exchangeRepository.findById(id + 1)).thenReturn(Optional.of(exchange));

        ExchangeDto res = exchangeService.getExchangeById(id);

        assertNull(res);
    }
}

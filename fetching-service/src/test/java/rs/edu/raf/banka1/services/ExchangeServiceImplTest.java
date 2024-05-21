package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import rs.edu.raf.banka1.repositories.CountryRepository;
import rs.edu.raf.banka1.repositories.ExchangeRepository;
import rs.edu.raf.banka1.repositories.HolidayRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

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

        exchangeService = new ExchangeServiceImpl(countryRepository, holidayRepository, exchangeRepository);
    }
}

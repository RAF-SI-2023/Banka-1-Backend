package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.mapper.CurrencyMapper;
import rs.edu.raf.banka1.model.entities.Currency;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.model.entities.Inflation;
import rs.edu.raf.banka1.model.exceptions.CurrencyNotFoundException;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.InflationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceUnitTests {

    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private CurrencyMapper currencyMapper;
    @Mock
    private InflationRepository inflationRepository;
    @InjectMocks
    private CurrencyService currencyService;

    @Test
    void findByIdSuccessfully() {
        // Arrange
        long currencyId = 1L;
        Currency currency = new Currency();
        currency.setId(currencyId);
        currency.setCurrencyCode("AED");
        currency.setCurrencyName("United Arab Emirates Dirham");

        when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(currency));
        when(currencyMapper.currencyToCurrencyDto(currency)).thenReturn(new CurrencyDto("United Arab Emirates Dirham", "AED"));

        // Act
        CurrencyDto result = currencyService.findById(currencyId);

        // Assert
        assertEquals("United Arab Emirates Dirham", result.getCurrencyName());
        assertEquals("AED", result.getCurrencyCode());
        verify(currencyRepository, times(1)).findById(currencyId);
    }

    @Test
    void findByIdWhenCurrencyNotFound() {
        // Arrange
        long currencyId = 1L;
        when(currencyRepository.findById(currencyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CurrencyNotFoundException.class, () -> currencyService.findById(currencyId));
        verify(currencyRepository, times(1)).findById(currencyId);
    }

    @Test
    void findAllSuccessfully() {
        // Arrange
        List<Currency> currencies = new ArrayList<>();
        List<Inflation> inflations = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            Currency myCurrency = new Currency();
            Random random = new Random();
            int currentYear = 2024;
            int numYears = random.nextInt(20) + 1;
            for (int j = 0; j < numYears; j++) {
                float inflationRate = random.nextFloat() * 10;
                inflations.add(new Inflation(currentYear - j, inflationRate, myCurrency));
            }

            if(i == 0) {
                myCurrency.setId(1L);
                myCurrency.setCurrencyCode("AED");
                myCurrency.setCurrencyName("United Arab Emirates Dirham");
            } else {
                myCurrency.setId(2L);
                myCurrency.setCurrencyCode("AFN");
                myCurrency.setCurrencyName("Afghan Afghani");
            }
            myCurrency.setInflations(inflations);
            currencies.add(myCurrency);
        }
        when(currencyRepository.findAll()).thenReturn(currencies);
        when(currencyMapper.currencyToCurrencyDto(any())).thenAnswer(
                invocation -> {
                    Currency currency = invocation.getArgument(0);
                    return new CurrencyDto(currency.getCurrencyName(), currency.getCurrencyCode());
                });

        // Act
        List<CurrencyDto> result = currencyService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(currencyRepository, times(1)).findAll();
    }

    @Test
    void findCurrencyByCurrencyNameSuccessfully() {
        var currency = new Currency();
        currency.setId(1L);
        currency.setCurrencyCode("AED");
        currency.setCurrencyName("United Arab Emirates Dirham");
        currency.setCurrencySymbol("AED");

        when(currencyRepository.findByCurrencyName(anyString())).thenReturn(Optional.of(currency));
        when(currencyMapper.currencyToCurrencyDto(currency)).thenReturn(new CurrencyDto("United Arab Emirates Dirham", "AED"));
        var result = currencyService.findCurrencyByCurrencyName("United Arab Emirates Dirham");

        assertEquals("United Arab Emirates Dirham", result.getCurrencyName());
        assertEquals("AED", result.getCurrencyCode());

        verify(currencyRepository, times(1)).findByCurrencyName(anyString());
    }

    @Test
    void findCurrencyByCurrencyCodeSuccessfully() {
        var currency = new Currency();
        currency.setId(1L);
        currency.setCurrencyCode("AED");
        currency.setCurrencyName("United Arab Emirates Dirham");
        currency.setCurrencySymbol("AED");

        when(currencyRepository.findCurrencyByCurrencyCode(anyString())).thenReturn(Optional.of(currency));
        when(currencyMapper.currencyToCurrencyDto(currency)).thenReturn(new CurrencyDto("United Arab Emirates Dirham", "AED"));
        var result = currencyService.findCurrencyByCurrencyCode("AED");

        assertEquals("United Arab Emirates Dirham", result.getCurrencyName());
        assertEquals("AED", result.getCurrencyCode());

        verify(currencyRepository, times(1)).findCurrencyByCurrencyCode(anyString());
    }
}

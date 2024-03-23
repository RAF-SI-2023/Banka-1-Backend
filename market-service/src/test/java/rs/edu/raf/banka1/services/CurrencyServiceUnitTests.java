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
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.InflationRepository;
import rs.edu.raf.banka1.services.CurrencyService;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    public void addCurrenciesSuccessfully() {
        List<CurrencyDto> currencyList = new ArrayList<>();
        currencyList.add(new CurrencyDto("AED", "United Arab Emirates Dirham"));
        currencyList.add(new CurrencyDto("AFN", "Afghan Afghani"));
        currencyList.add(new CurrencyDto("ALL", "Albanian Lek"));

        currencyService.addCurrencies(currencyList);

        verify(currencyRepository, times(1)).saveAll((Mockito.anyCollection()));
        verifyNoMoreInteractions(currencyRepository);
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
        verifyNoMoreInteractions(currencyRepository);
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
        verifyNoMoreInteractions(currencyRepository);
    }

}

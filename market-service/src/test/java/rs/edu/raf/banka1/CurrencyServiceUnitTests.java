package rs.edu.raf.banka1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.model.entities.Currency;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.services.CurrencyService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceUnitTests {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyService currencyService;

    @Test
    public void addCurrenciesSuccessfully(){
        List<CurrencyDto> currencyList = new ArrayList<>();
        currencyList.add(new CurrencyDto("AED", "United Arab Emirates Dirham"));
        currencyList.add(new CurrencyDto("AFN", "Afghan Afghani"));
        currencyList.add(new CurrencyDto("ALL", "Albanian Lek"));

        currencyService.addCurrencies(currencyList);

        verify(currencyRepository, times(1)).saveAll((Mockito.anyCollection()));
        verifyNoMoreInteractions(currencyRepository);
    }

    @Test
    void findCurrencyByCurrencyNameSuccessfully(){
        var currency = new Currency();
        currency.setId(1L);
        currency.setCurrencyCode("AED");
        currency.setCurrencyName("United Arab Emirates Dirham");
        currency.setCurrencySymbol("AED");

        when(currencyRepository.findByCurrencyName(anyString())).thenReturn(Optional.of(currency));
        var result = currencyService.findCurrencyByCurrencyName("United Arab Emirates Dirham");

        assertEquals("United Arab Emirates Dirham", result.getCurrencyName());
        assertEquals("AED", result.getCurrencyCode());

        verify(currencyRepository, times(1)).findByCurrencyName(anyString());
        verifyNoMoreInteractions(currencyRepository);
    }

    @Test
    void findCurrencyByCurrencyCodeSuccessfully(){
        var currency = new Currency();
        currency.setId(1L);
        currency.setCurrencyCode("AED");
        currency.setCurrencyName("United Arab Emirates Dirham");
        currency.setCurrencySymbol("AED");

        when(currencyRepository.findCurrencyByCurrencyCode(anyString())).thenReturn(Optional.of(currency));
        var result = currencyService.findCurrencyByCurrencyCode("AED");

        assertEquals("United Arab Emirates Dirham", result.getCurrencyName());
        assertEquals("AED", result.getCurrencyCode());

        verify(currencyRepository, times(1)).findCurrencyByCurrencyCode(anyString());
        verifyNoMoreInteractions(currencyRepository);
    }

}

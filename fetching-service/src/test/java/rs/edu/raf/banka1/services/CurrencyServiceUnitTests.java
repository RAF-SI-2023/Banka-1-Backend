package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.InflationRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceUnitTests {

    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private InflationRepository inflationRepository;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Test
    public void addCurrenciesSuccessfully() {
        List<CurrencyDto> currencyList = new ArrayList<>();
        currencyList.add(new CurrencyDto("AED", "United Arab Emirates Dirham"));
        currencyList.add(new CurrencyDto("AFN", "Afghan Afghani"));
        currencyList.add(new CurrencyDto("ALL", "Albanian Lek"));

        currencyService.addCurrencies(currencyList);

        verify(currencyRepository, times(1)).saveAll((Mockito.anyCollection()));
//        verifyNoMoreInteractions(currencyRepository);
    }
}

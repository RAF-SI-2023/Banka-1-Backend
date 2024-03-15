package rs.edu.raf.banka1;

import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rs.edu.raf.banka1.model.ExchangeModel;
import rs.edu.raf.banka1.repositories.ExchangeRepository;
import rs.edu.raf.banka1.services.ExchangeService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class ExchangeServiceUnitTests {
    @Mock
    private ExchangeRepository exchangeRepository;

    @InjectMocks
    private ExchangeService exchangeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); // Initialize mocks
//         exchangeRepository = mock(ExchangeRepository.class);
//         exchangeService = new ExchangeService(exchangeRepository);
    }

    @Test
    public void testParseExchangeCSV() {
        String csvData = "DRSP,\"DRSP\",\"OPRT\",\"EURONEXT UK - REPORTING SERVICES\",\"EURONEXT LONDON LIMITED\",\"969500HMVSZ0TCV65D58\",\"APPA\",\"\",\"GB\",\"LONDON\",\"WWW.EURONEXT.COM\",\"ACTIVE\",\"20210927\",\"20210927\",\"20210927\",\"\",\"APPROVED PUBLICATION ARRANGEMENT.\"\n" +
                "XCNQ,\"XCNQ\",\"OPRT\",\"CANADIAN SECURITIES EXCHANGE\",\"CNSX MARKETS, INC.\",\"\",\"RMKT\",\"CSE LISTED\",\"CA\",\"TORONTO\",\"WWW.THECSE.COM\",\"ACTIVE\",\"20090427\",\"20210927\",\"20210927\",\"\",\"FORMERLY KNOWN AS PURE (CSE OTHER LISTED) FORMERLY KNOWN AS THE CANADIAN NATIONAL STOCK EXCHANGE (CNSX).\"";

        // Call the method to be tested
        exchangeService.parseExchangeCSV(csvData);

        // Verify that saveAll() is called only once with anyList() as argument
        verify(exchangeRepository, times(2)).saveAll(anyList());
    }

    @Test
    public void testInitTimes() {
        int expectedTimeZones = 45;

        exchangeService.initTimes();

        assertEquals(expectedTimeZones, exchangeService.getMarketOpenTimes().size());
        assertEquals(expectedTimeZones, exchangeService.getMarketCloseTimes().size());
    }

    @Test
    public void testSaveExchangeModel() {
        // Create a sample ExchangeModel
        ExchangeModel exchangeModel = new ExchangeModel();
        exchangeModel.setMicCode("MIC1");
        exchangeModel.setExchangeName("Exchange1");
        // Set other properties...

        // Call the method to be tested
        exchangeService.saveExchangeModel(exchangeModel);

        // Verify that save() is called only once with the specified exchangeModel
        verify(exchangeRepository, times(1)).save(any(ExchangeModel.class));

        // Capture the argument passed to save()
        ArgumentCaptor<ExchangeModel> captor = ArgumentCaptor.forClass(ExchangeModel.class);
        verify(exchangeRepository).save(captor.capture());

        // Retrieve the captured ExchangeModel
        ExchangeModel capturedModel = captor.getValue();

        // Assert that the captured ExchangeModel is equal to the one passed
        assertEquals(exchangeModel, capturedModel);
    }
}

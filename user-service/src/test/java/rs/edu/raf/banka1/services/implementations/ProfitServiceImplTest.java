package rs.edu.raf.banka1.services.implementations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.repositories.StockProfitRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.bouncycastle.util.Longs.valueOf;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProfitServiceImplTest {
    @Mock
    StockProfitRepository stockProfitRepository;

    @InjectMocks
    ProfitServiceImpl profitService;

    @Test
    public void testGetStockProfitBank() {
        profitService.getStockProfitBank();
        verify(stockProfitRepository, times(1)).findAll();
    }

    @Test
    public void testGetStockProfitAgent() {
        profitService.getStockProfitAgent(valueOf(1));
        verify(stockProfitRepository, times(1)).findAll();
    }
}

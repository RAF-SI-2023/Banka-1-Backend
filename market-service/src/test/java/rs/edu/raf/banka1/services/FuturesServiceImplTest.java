package rs.edu.raf.banka1.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import rs.edu.raf.banka1.mapper.FutureMapper;
import rs.edu.raf.banka1.mapper.StockMapper;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.dtos.ListingFutureDto;
import rs.edu.raf.banka1.repositories.FutureRepository;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.repositories.StockRepository;

import java.sql.Date;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class FuturesServiceImplTest {
    @Mock
    private ListingHistoryRepository listingHistoryRepository;
    @Mock
    private FutureMapper futureMapper;
    @Mock
    private FutureRepository futureRepository;
    @Mock
    private ListingStockService listingStockService;
    @InjectMocks
    private FuturesServiceImpl futuresService;
    private ListingFuture future1;
    private ListingFuture future2;
    private List<ListingFuture> futures;
    @BeforeEach
    public void setUp(){

        // stock data
        future1 = new ListingFuture();
        future1.setTicker("ESM24");
        future1.setPrice(100.0);

        future2 = new ListingFuture();
        future2.setTicker("YMM24");
        future2.setPrice(200.0);

        futures = new ArrayList<>();
        futures.add(future1);
        futures.add(future2);
    }


    @Test
    public void getListingHistoriesByTimestampNoResults(){

        ListingFuture listingFuture = new ListingFuture();
        listingFuture.setTicker("ESM24");
        listingFuture.setAlternativeTicker("ESM24");
        when(futureRepository.findById(1L)).thenReturn(Optional.of(listingFuture));
        when(listingHistoryRepository.getListingHistoriesByTicker("ESM24")).thenReturn(new ArrayList<>());
//
//
//        //fetchnsinglefuturehistorysetup
//        WebElement table = mock(WebElement.class);
//
//        List<WebElement> rows = new ArrayList<>();
//        for(int i = 0;i<50;i++){
//            WebElement row = mock(WebElement.class);
//            List<WebElement> cells = new ArrayList<>();
//            WebElement datecell = mock(WebElement.class);
//            when(datecell.getText()).thenReturn("JAN 11, 2024");
//            cells.add(datecell);
//            for(int j =1;j<7;j++){
//                WebElement cell = mock(WebElement.class);
//                when(cell.getText()).thenReturn("100");
//                cells.add(cell);
//            }
//            when(row.findElements(any())).thenReturn(cells);
//            rows.add(row);
//        }
//        when(table.findElements(By.tagName("tr"))).thenReturn(rows);
//
//        when(driver.findElement(By.cssSelector("div.table-container.svelte-ta1t6m"))).thenReturn(table);

        List<ListingHistory> result = futuresService.getListingHistoriesByTimestamp(1L, 1, 2);

        verify(listingHistoryRepository, times(1)).getListingHistoriesByTickerAndDateBetween(any(), any(), any());
        verify(listingHistoryRepository, times(0)).getListingHistoriesByTickerAndDateAfter(any(), any());
        verify(listingHistoryRepository, times(0)).getListingHistoriesByTickerAndDateBefore(any(), any());

    }

    @Test
    public void getListingHistoriesByTimestampNoFrom(){

        ListingFuture listingFuture = new ListingFuture();
        listingFuture.setTicker("ESM24");
        listingFuture.setAlternativeTicker("ESM24");
        when(futureRepository.findById(1L)).thenReturn(Optional.of(listingFuture));
        when(listingHistoryRepository.getListingHistoriesByTicker("ESM24")).thenReturn(new ArrayList<>());


//        //fetchnsinglefuturehistorysetup
//        WebElement table = mock(WebElement.class);
//
//        List<WebElement> rows = new ArrayList<>();
//        for(int i = 0;i<50;i++){
//            WebElement row = mock(WebElement.class);
//            List<WebElement> cells = new ArrayList<>();
//            WebElement datecell = mock(WebElement.class);
//            when(datecell.getText()).thenReturn("JAN 11, 2024");
//            cells.add(datecell);
//            for(int j =1;j<7;j++){
//                WebElement cell = mock(WebElement.class);
//                when(cell.getText()).thenReturn("100");
//                cells.add(cell);
//            }
//            when(row.findElements(any())).thenReturn(cells);
//            rows.add(row);
//        }
//        when(table.findElements(By.tagName("tr"))).thenReturn(rows);
//
//        when(driver.findElement(By.cssSelector("div.table-container.svelte-ta1t6m"))).thenReturn(table);

        List<ListingHistory> result = futuresService.getListingHistoriesByTimestamp(1L, null, 2);


        verify(listingHistoryRepository, times(0)).getListingHistoriesByTickerAndDateBetween(any(), any(), any());
        verify(listingHistoryRepository, times(0)).getListingHistoriesByTickerAndDateAfter(any(), any());
        verify(listingHistoryRepository, times(1)).getListingHistoriesByTickerAndDateBefore(any(), any());
    }

    @Test
    public void getListingHistoriesByTimestampNoTo(){

        ListingFuture listingFuture = new ListingFuture();
        listingFuture.setTicker("ESM24");
        listingFuture.setAlternativeTicker("ESM24");
        when(futureRepository.findById(1L)).thenReturn(Optional.of(listingFuture));
        when(listingHistoryRepository.getListingHistoriesByTicker("ESM24")).thenReturn(new ArrayList<>());


//        //fetchnsinglefuturehistorysetup
//        WebElement table = mock(WebElement.class);
//
//        List<WebElement> rows = new ArrayList<>();
//        for(int i = 0;i<50;i++){
//            WebElement row = mock(WebElement.class);
//            List<WebElement> cells = new ArrayList<>();
//            WebElement datecell = mock(WebElement.class);
//            when(datecell.getText()).thenReturn("JAN 11, 2024");
//            cells.add(datecell);
//            for(int j =1;j<7;j++){
//                WebElement cell = mock(WebElement.class);
//                when(cell.getText()).thenReturn("100");
//                cells.add(cell);
//            }
//            when(row.findElements(any())).thenReturn(cells);
//            rows.add(row);
//        }
//        when(table.findElements(By.tagName("tr"))).thenReturn(rows);
//
//        when(driver.findElement(By.cssSelector("div.table-container.svelte-ta1t6m"))).thenReturn(table);

        List<ListingHistory> result = futuresService.getListingHistoriesByTimestamp(1L, 1, null);


        verify(listingHistoryRepository, times(0)).getListingHistoriesByTickerAndDateBetween(any(), any(), any());
        verify(listingHistoryRepository, times(1)).getListingHistoriesByTickerAndDateAfter(any(), any());
        verify(listingHistoryRepository, times(0)).getListingHistoriesByTickerAndDateBefore(any(), any());
    }

    @Test
    public void getAllFutures(){
        when(futureRepository.findAll()).thenReturn(futures);
        List<ListingFuture> listingFutures = futuresService.getAllFutures();
        assertThat(listingFutures).isNotNull();
        assertThat(listingFutures).hasSize(2);
    }
}

package rs.edu.raf.banka1.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import rs.edu.raf.banka1.mapper.FutureMapper;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.repositories.FutureRepository;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    @Mock
    private DriverService driverService;
    //@InjectMocks
    private FuturesServiceImpl futuresService;
    private ListingFuture future1;
    private ListingFuture future2;
    private List<ListingFuture> futures;
    @Mock
    private WebDriver driver;

    @BeforeEach
    public void setUp(){

        try(MockedStatic<WebDriverManager> webDriverManagerMockedStatic = mockStatic(WebDriverManager.class)){
            WebDriverManager webDriverManager = mock(WebDriverManager.class);
            webDriverManagerMockedStatic.when(WebDriverManager::chromedriver).thenReturn(webDriverManager);
            when(driverService.createNewDriver()).thenReturn(driver);
            futuresService = new FuturesServiceImpl(futureRepository, listingHistoryRepository, futureMapper, listingStockService, driverService);
        }

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

    // puca test
    @Test
    @Disabled
    public void fetchNFutureHistores(){
        WebElement table = mock(WebElement.class);

        List<WebElement> rows = new ArrayList<>();
        for(int i = 0;i<50;i++){
            WebElement row = mock(WebElement.class);
            List<WebElement> cells = new ArrayList<>();
            WebElement datecell = mock(WebElement.class);
            when(datecell.getText()).thenReturn("JAN 11, 2024");
            cells.add(datecell);
            for(int j =1;j<7;j++){
                WebElement cell = mock(WebElement.class);
                when(cell.getText()).thenReturn("100");
                cells.add(cell);
            }
            when(row.findElements(any())).thenReturn(cells);
            rows.add(row);
        }
        when(table.findElements(By.tagName("tr"))).thenReturn(rows);

        when(driver.findElement(By.cssSelector("div.table-container.svelte-ta1t6m"))).thenReturn(table);

        List<ListingFuture> listingFutures = new ArrayList<>();
        ListingFuture listingFuture = new ListingFuture();
        listingFuture.setTicker("ESM24");
        listingFuture.setAlternativeTicker("ESM24");
        listingFutures.add(listingFuture);

        List<ListingHistory> listingHistories = futuresService.fetchNFutureHistories(listingFutures, 1);

        assertThat(listingHistories).isNotNull();
        assertThat(listingHistories).hasSize(1);
    }


    @Test
    public void fetchNFuturesNY_Mercantile(){
        WebElement tableElement = mock(WebElement.class);
        when(driver.findElement(By.xpath("//table[@class='W(100%)']")))
                .thenReturn(tableElement);
        List<WebElement> webElements = new ArrayList<>();
        for(int i = 0;i<4;i++){
            WebElement row = mock(WebElement.class);
            List<WebElement> cells = new ArrayList<>();
            for(int j =0;j<3;j++){
                WebElement cell = mock(WebElement.class);
                if(j!=2) {
                    when(cell.getText()).thenReturn("testtext");
                }
                else{
                    when(cell.getText()).thenReturn("100.0");
                }
                cells.add(cell);
            }
            when(row.findElements(any())).thenReturn(cells);
            webElements.add(row);
        }
        when(tableElement.findElements(any())).thenReturn(webElements);

        WebElement tableDiv = mock(WebElement.class);
        when(driver.findElement(By.cssSelector("div.container.svelte-tx3nkj"))).thenReturn(tableDiv);
        List<WebElement> tableRows = new ArrayList<>();
        when(tableDiv.findElements(By.tagName("li"))).thenReturn(tableRows);
        for(int i =0;i<8;i++){
            WebElement row = mock(WebElement.class);
            WebElement content = mock(WebElement.class);
            if(i == 3 || i == 7 || i==6 || i==4){
                when(content.getText()).thenReturn("100");
            }
            else if (i==1){
                when(content.getText()).thenReturn("2018-12-27");
            }
            else {
                when(content.getText()).thenReturn("text");
            }
            when(row.findElement(By.className("value"))).thenReturn(content);
            tableRows.add(row);
        }

        when(driver.getPageSource()).thenReturn("NY Mercantile");

        List<ListingFuture> listingFutures = futuresService.fetchNFutures(2);

        assertThat(listingFutures).isNotNull();
        assertThat(listingFutures).hasSize(2);
    }

    @Test
    public void fetchNFuturesCME(){
        WebElement tableElement = mock(WebElement.class);
        when(driver.findElement(By.xpath("//table[@class='W(100%)']")))
                .thenReturn(tableElement);
        List<WebElement> webElements = new ArrayList<>();
        for(int i = 0;i<4;i++){
            WebElement row = mock(WebElement.class);
            List<WebElement> cells = new ArrayList<>();
            for(int j =0;j<3;j++){
                WebElement cell = mock(WebElement.class);
                if(j!=2) {
                    when(cell.getText()).thenReturn("testtext");
                }
                else{
                    when(cell.getText()).thenReturn("100.0");
                }
                cells.add(cell);
            }
            when(row.findElements(any())).thenReturn(cells);
            webElements.add(row);
        }
        when(tableElement.findElements(any())).thenReturn(webElements);

        WebElement tableDiv = mock(WebElement.class);
        when(driver.findElement(By.cssSelector("div.container.svelte-tx3nkj"))).thenReturn(tableDiv);
        List<WebElement> tableRows = new ArrayList<>();
        when(tableDiv.findElements(By.tagName("li"))).thenReturn(tableRows);
        for(int i =0;i<8;i++){
            WebElement row = mock(WebElement.class);
            WebElement content = mock(WebElement.class);
            if(i == 3 || i == 7 || i==6 || i==4){
                when(content.getText()).thenReturn("100");
            }
            else if (i==1){
                when(content.getText()).thenReturn("2018-12-27");
            }
            else {
                when(content.getText()).thenReturn("text");
            }
            when(row.findElement(By.className("value"))).thenReturn(content);
            tableRows.add(row);
        }

        when(driver.getPageSource()).thenReturn("CME");

        List<ListingFuture> listingFutures = futuresService.fetchNFutures(2);

        assertThat(listingFutures).isNotNull();
        assertThat(listingFutures).hasSize(2);
    }

    @Test
    public void fetchNFuturesCBOT(){
        WebElement tableElement = mock(WebElement.class);
        when(driver.findElement(By.xpath("//table[@class='W(100%)']")))
                .thenReturn(tableElement);
        List<WebElement> webElements = new ArrayList<>();
        for(int i = 0;i<4;i++){
            WebElement row = mock(WebElement.class);
            List<WebElement> cells = new ArrayList<>();
            for(int j =0;j<3;j++){
                WebElement cell = mock(WebElement.class);
                if(j!=2) {
                    when(cell.getText()).thenReturn("testtext");
                }
                else{
                    when(cell.getText()).thenReturn("100.0");
                }
                cells.add(cell);
            }
            when(row.findElements(any())).thenReturn(cells);
            webElements.add(row);
        }
        when(tableElement.findElements(any())).thenReturn(webElements);

        WebElement tableDiv = mock(WebElement.class);
        when(driver.findElement(By.cssSelector("div.container.svelte-tx3nkj"))).thenReturn(tableDiv);
        List<WebElement> tableRows = new ArrayList<>();
        when(tableDiv.findElements(By.tagName("li"))).thenReturn(tableRows);
        for(int i =0;i<8;i++){
            WebElement row = mock(WebElement.class);
            WebElement content = mock(WebElement.class);
            if(i == 3 || i == 7 || i==6 || i==4){
                when(content.getText()).thenReturn("100");
            }
            else if (i==1){
                when(content.getText()).thenReturn("2018-12-27");
            }
            else {
                when(content.getText()).thenReturn("text");
            }
            when(row.findElement(By.className("value"))).thenReturn(content);
            tableRows.add(row);
        }

        when(driver.getPageSource()).thenReturn("CBOT");

        List<ListingFuture> listingFutures = futuresService.fetchNFutures(2);

        assertThat(listingFutures).isNotNull();
        assertThat(listingFutures).hasSize(2);
    }

    @Test
    public void fetchNFuturesCOMEX(){
        WebElement tableElement = mock(WebElement.class);
        when(driver.findElement(By.xpath("//table[@class='W(100%)']")))
                .thenReturn(tableElement);
        List<WebElement> webElements = new ArrayList<>();
        for(int i = 0;i<4;i++){
            WebElement row = mock(WebElement.class);
            List<WebElement> cells = new ArrayList<>();
            for(int j =0;j<3;j++){
                WebElement cell = mock(WebElement.class);
                if(j!=2) {
                    when(cell.getText()).thenReturn("testtext");
                }
                else{
                    when(cell.getText()).thenReturn("100.0");
                }
                cells.add(cell);
            }
            when(row.findElements(any())).thenReturn(cells);
            webElements.add(row);
        }
        when(tableElement.findElements(any())).thenReturn(webElements);

        WebElement tableDiv = mock(WebElement.class);
        when(driver.findElement(By.cssSelector("div.container.svelte-tx3nkj"))).thenReturn(tableDiv);
        List<WebElement> tableRows = new ArrayList<>();
        when(tableDiv.findElements(By.tagName("li"))).thenReturn(tableRows);
        for(int i =0;i<8;i++){
            WebElement row = mock(WebElement.class);
            WebElement content = mock(WebElement.class);
            if(i == 3 || i == 7 || i==6 || i==4){
                when(content.getText()).thenReturn("100");
            }
            else if (i==1){
                when(content.getText()).thenReturn("2018-12-27");
            }
            else {
                when(content.getText()).thenReturn("text");
            }
            when(row.findElement(By.className("value"))).thenReturn(content);
            tableRows.add(row);
        }

        when(driver.getPageSource()).thenReturn("COMEX");

        List<ListingFuture> listingFutures = futuresService.fetchNFutures(2);

        assertThat(listingFutures).isNotNull();
        assertThat(listingFutures).hasSize(2);
    }



    @Test
    public void addListingStockNotPresentTest(){
        when(futuresService.findByTicker("ESM24")).thenReturn(Optional.empty());
        assertEquals(true, futuresService.addFuture(future1));
    }

    @Test
    public void addListingStockPresentTest(){
        ListingFuture updateFuture = new ListingFuture();
        updateFuture.setTicker("ESM24");
        updateFuture.setPrice(101.0);
        when(futuresService.findByTicker("ESM24")).thenReturn(Optional.of(future1));
        assertEquals(false, futuresService.addFuture(updateFuture));
    }

    @Test
    public void addAllListingStocksPresentTests(){
        when(futuresService.findByTicker("ESM24")).thenReturn(Optional.of(future1));
        when(futuresService.findByTicker("YMM24")).thenReturn(Optional.of(future2));
        assertEquals(0, futuresService.addAllFutures(futures));
    }

    @Test
    public void addAllListingStocksNotPresentTests(){
        when(futuresService.findByTicker("ESM24")).thenReturn(Optional.empty());
        when(futuresService.findByTicker("YMM24")).thenReturn(Optional.empty());
        assertEquals(futures.size(), futuresService.addAllFutures(futures));
    }

}

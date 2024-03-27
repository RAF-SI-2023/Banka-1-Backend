package rs.edu.raf.banka1.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.FutureMapper;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.dtos.ListingFutureDto;
import rs.edu.raf.banka1.repositories.FutureRepository;

import java.time.*;
import java.util.*;

@Service
public class FuturesServiceImpl implements FuturesService {
    private WebDriver driver;
    private final Map<String, String> monthsCode = new HashMap<>();

    private final FutureRepository futureRepository;
    private final FutureMapper futureMapper;
    @Autowired
    public FuturesServiceImpl(FutureRepository futureRepository, FutureMapper futureMapper) {
        this.futureRepository = futureRepository;
        this.futureMapper = futureMapper;
    }

    @Override
    public List<ListingFuture> fetchNFutures(int n) {
        initializeScraper();
        // Instantiate ChromeDriver
        driver = new ChromeDriver();

        var rows = scrapeFuturesTable().subList(0, n);
        var futureUrls = extractFutureUrls(rows).subList(0, n);
        var futureTickers = extractFutureTickers(rows).subList(0, n);
        var futureNames = extractFutureNames(rows).subList(0, n);
        var futurePrices = extractFuturePrices(rows).subList(0, n);

        List<ListingFutureDto> listingFutureDtos = new ArrayList<>();
        for (String url : futureUrls) {
            ListingFutureDto listingFutureDto = scrapeFuture(url);
            listingFutureDtos.add(listingFutureDto);
        }

        int i = 0;
        for (var listingFutureDto : listingFutureDtos) {
            listingFutureDto.setTicker(futureTickers.get(i));
            listingFutureDto.setPrice(futurePrices.get(i));
            listingFutureDto.setPriceChange(futurePrices.get(i) - listingFutureDto.getLastPrice());
            listingFutureDto.setName(futureNames.get(i));
            reformatTicker(listingFutureDto);
            truncateName(listingFutureDto);
            i++;
        }

        // Close the WebDriver
        driver.quit();
        List<ListingFuture> listingFutures = new ArrayList<>();
        for (var dto : listingFutureDtos) {
            var listingFuture = futureMapper.futureDtoToFutureModel(dto);
            listingFutures.add(listingFuture);
        }
        return listingFutures;
    }

    void initializeScraper() {
        // Use WebDriverManager to setup ChromeDriver
        WebDriverManager.chromedriver().setup();

        monthsCode.put("Jan", "F");
        monthsCode.put("Feb", "G");
        monthsCode.put("Mar", "H");
        monthsCode.put("Apr", "J");
        monthsCode.put("May", "K");
        monthsCode.put("Jun", "M");
        monthsCode.put("Jul", "N");
        monthsCode.put("Aug", "Q");
        monthsCode.put("Sep", "U");
        monthsCode.put("Oct", "V");
        monthsCode.put("Nov", "X");
        monthsCode.put("Dec", "Z");
    }

    private List<WebElement> scrapeFuturesTable() {
        // Navigate to the webpage
        driver.get("https://finance.yahoo.com/commodities/");

        WebElement table = driver.findElement(By.xpath("//table[@class='W(100%)']"));

        // Locate all rows in the table
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        rows.removeFirst();
        return rows;
    }

    private List<String> extractFutureUrls(List<WebElement> rows) {
        List<String> urls = new ArrayList<>();
        for (WebElement row : rows) {
            // Locate all cells in the row
            List<WebElement> cells = row.findElements(By.tagName("td"));
            urls.add("https://finance.yahoo.com/quote/" + cells.getFirst().getText().replace("=", "%3D"));
        }
        return urls;
    }

    private List<String> extractFutureTickers(List<WebElement> rows) {
        List<String> tickers = new ArrayList<>();
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            tickers.add(cells.getFirst().getText());
        }
        return tickers;
    }

    private List<String> extractFutureNames(List<WebElement> rows) {
        List<String> names = new ArrayList<>();
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            names.add(cells.get(1).getText());
        }
        return names;
    }

    private List<Double> extractFuturePrices(List<WebElement> rows) {
        List<Double> prices = new ArrayList<>();
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            prices.add(Double.parseDouble(cells.get(2).getText().replaceAll(",", "")));
        }
        return prices;
    }

    private ListingFutureDto scrapeFuture(String url) {
        // Navigate to the webpage
        driver.get(url);

        // Find the quote summary element
        WebElement quoteSummary = driver.findElement(By.id("quote-summary"));

        // Find elements in the left summary table
        WebElement leftSummaryTable = quoteSummary.findElement(By.cssSelector("[data-test=left-summary-table]"));
        String settlementDate = leftSummaryTable.findElement(By.cssSelector("[data-test=SETTLEMENT_DATE-value]")).getText();
        String bid = leftSummaryTable.findElement(By.cssSelector("[data-test=BID-value]")).getText();

        // Find elements in the right summary table
        WebElement rightSummaryTable = quoteSummary.findElement(By.cssSelector("[data-test=right-summary-table]"));
        String lastPrice = rightSummaryTable.findElement(By.cssSelector("[data-test=LAST_PRICE-value]")).getText();
        String volume = rightSummaryTable.findElement(By.cssSelector("[data-test=TD_VOLUME-value]")).getText();
        String ask = rightSummaryTable.findElement(By.cssSelector("[data-test=ASK-value]")).getText();


        ListingFutureDto listingFutureDto = new ListingFutureDto();
        listingFutureDto.setHigh(Double.parseDouble(ask.replaceAll(",", "")));
        listingFutureDto.setLow(Double.parseDouble(bid.replaceAll(",", "")));
        listingFutureDto.setSettlementDate((int) LocalDate.parse(settlementDate).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
        listingFutureDto.setVolume(Integer.parseInt(volume.replaceAll(",", "")));
        listingFutureDto.setLastPrice(Double.parseDouble(lastPrice.replaceAll(",", "")));

        String pageSource = driver.getPageSource();
        if (pageSource.contains("NY Mercantile")) {
            listingFutureDto.setExchange("NYMEX");
        }
        if (pageSource.contains("CME")) {
            listingFutureDto.setExchange("CME");
        }
        if (pageSource.contains("CBOT")) {
            listingFutureDto.setExchange("CBOT");
        }
        if (pageSource.contains("COMEX")) {
            listingFutureDto.setExchange("COMEX");
        }

        return listingFutureDto;
    }

    private void reformatTicker(ListingFutureDto listingFutureDto) {
        String ticker = listingFutureDto.getTicker();
        ticker = ticker.substring(0, ticker.length() - 2);

        Instant instant = Instant.ofEpochSecond(listingFutureDto.getSettlementDate());
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        StringBuilder sb = new StringBuilder(ticker);
        String monthShort = ldt.getMonth().toString().substring(0, 3).toLowerCase();
        sb.append(monthsCode.get(monthShort.substring(0, 1).toUpperCase() + monthShort.substring(1)));
        sb.append(ldt.getYear() % 100);
        listingFutureDto.setTicker(sb.toString());
    }

    private void truncateName(ListingFutureDto listingFutureDto) {
        String name = listingFutureDto.getName();
        for (var month : monthsCode.keySet()) {
            String[] split = name.split(month);
            name = split[0];
            if (name.endsWith(",")) {
                name = name.substring(0, name.length() - 1);
            }
        }
        listingFutureDto.setName(name);
    }

    @Override
    public int addAllFutures(List<ListingFuture> futures) {
        return futures.stream().mapToInt(this::addFuture).sum();
    }
    @Override
    public int addFuture(ListingFuture future) {
        Optional<ListingFuture> optionalFuture = futureRepository.findByTicker(future.getTicker());
        if (optionalFuture.isPresent()) {
            var oldFuture = optionalFuture.get();
            futureMapper.updateFuture(oldFuture, future);
            futureRepository.save(oldFuture);
            return 0;
        }else{
            futureRepository.save(future);
            return 1;
        }
    }
}

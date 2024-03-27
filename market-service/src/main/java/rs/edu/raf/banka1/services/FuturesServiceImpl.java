package rs.edu.raf.banka1.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.FutureMapper;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.dtos.ListingFutureDto;
import rs.edu.raf.banka1.repositories.FutureRepository;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

@Service
public class FuturesServiceImpl implements FuturesService {
    private final Map<String, String> monthsCode = new HashMap<>();
    private final FutureRepository futureRepository;
    private final ListingHistoryRepository listingHistoryRepository;
    private final FutureMapper futureMapper;
    private final ChromeOptions options;
    private final ListingStockService listingStockService;
    @Autowired
    public FuturesServiceImpl(FutureRepository futureRepository, ListingHistoryRepository listingHistoryRepository, FutureMapper futureMapper, ListingStockService listingStockService) {
        this.futureRepository = futureRepository;
        this.listingHistoryRepository = listingHistoryRepository;
        this.futureMapper = futureMapper;
        this.listingStockService = listingStockService;

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
        options = new ChromeOptions();
        options.addArguments("--headless");
    }


    @Override
    public List<ListingFuture> fetchNFutures(int n) {

        WebDriver driver = new ChromeDriver(options);

        var rows = scrapeFuturesTable(driver).subList(0, n);
        var futureUrls = extractFutureUrls(rows).subList(0, n);
        var futureTickers = extractFutureTickers(rows).subList(0, n);
        var futureNames = extractFutureNames(rows).subList(0, n);
        var futurePrices = extractFuturePrices(rows).subList(0, n);

        List<ListingFutureDto> listingFutureDtos = new ArrayList<>();
        for (String url : futureUrls) {
            ListingFutureDto listingFutureDto = scrapeFuture(url, driver);
            listingFutureDtos.add(listingFutureDto);
        }

        int i = 0;
        for (var listingFutureDto : listingFutureDtos) {
            listingFutureDto.setTicker(futureTickers.get(i));
            listingFutureDto.setAlternativeTicker(futureTickers.get(i));
            listingFutureDto.setPrice(futurePrices.get(i));
            listingFutureDto.setPriceChange(futurePrices.get(i) - listingFutureDto.getLastPrice());
            listingFutureDto.setName(futureNames.get(i));
            reformatTicker(listingFutureDto);
            truncateName(listingFutureDto);
            i++;
        }

        List<ListingFuture> listingFutures = new ArrayList<>();
        for (var dto : listingFutureDtos) {
            var listingFuture = futureMapper.futureDtoToFutureModel(dto);
            listingFutures.add(listingFuture);
        }

        driver.quit();
        return listingFutures;
    }

    private List<WebElement> scrapeFuturesTable(WebDriver driver) {
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

    private ListingFutureDto scrapeFuture(String url, WebDriver driver) {
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
            listingFutureDto.setExchangeName("NYMEX");
        }
        if (pageSource.contains("CME")) {
            listingFutureDto.setExchangeName("CME");
        }
        if (pageSource.contains("CBOT")) {
            listingFutureDto.setExchangeName("CBOT");
        }
        if (pageSource.contains("COMEX")) {
            listingFutureDto.setExchangeName("COMEX");
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
            name = name.trim();
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
        }
        else{
            futureRepository.save(future);
            return 1;
        }
    }

    @Override
    public List<ListingHistory> fetchNFutureHistories(List<ListingFuture> listingFutures, int n) {
        WebDriver driver = new ChromeDriver(options);
        List<ListingHistory> histories = new ArrayList<>();
        for (var future: listingFutures) {
            histories.addAll(Objects.requireNonNull(fetchNSingleFutureHistory(future, n, driver)));
        }
        driver.quit();
        return histories;
    }

    private List<ListingHistory> fetchNSingleFutureHistory(ListingFuture future, int n, WebDriver driver) {
        String url = "https://finance.yahoo.com/quote/" + future.getAlternativeTicker().replace("=", "%3D") + "/history";
        driver.get(url);
        WebElement table = driver.findElement(By.className("W(100%)"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        rows.removeFirst();
        rows = rows.subList(0, n);

        List<ListingHistory> history = new ArrayList<>();
        for (WebElement row : rows) {
            ArrayList<String> cells = new ArrayList<>();
            for (WebElement cell : row.findElements(By.tagName("td"))) {
                cells.add(cell.getText());
            }
            ListingHistory singleHistory = new ListingHistory();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                Date date = sdf.parse(cells.getFirst());
                long millis = date.getTime();
                singleHistory.setDate(millis);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

            singleHistory.setTicker(future.getTicker());
            singleHistory.setPrice(Double.parseDouble(cells.get(1).replaceAll(",", "")));
            singleHistory.setHigh(Double.parseDouble(cells.get(2).replaceAll(",", "")));
            singleHistory.setLow(Double.parseDouble(cells.get(3).replaceAll(",", "")));
            singleHistory.setChanged(Double.parseDouble(cells.get(4).replaceAll(",", "")) - Double.parseDouble(cells.get(1).replaceAll(",", "")));
            singleHistory.setVolume(Integer.parseInt(cells.get(6).replaceAll(",", "")));
            history.add(singleHistory);
        }
        return history;
    }

    @Override
    public Optional<ListingFuture> findById(Long id) {
        return futureRepository.findById(id);
    }

    @Override
    public List<ListingHistory> getListingHistoriesByTimestamp(Long id, Integer from, Integer to) {
        List<ListingHistory> listingHistories = new ArrayList<>();
        ListingFuture future = futureRepository.findById(id).orElse(null);
        if(future == null){
            return listingHistories;
        }

        String ticker = future.getTicker();
        listingHistories = listingHistoryRepository.getListingHistoriesByTicker(ticker);
        if(listingHistories.isEmpty()) {
            WebDriver driver = new ChromeDriver(options);
            listingHistories = fetchNSingleFutureHistory(future, 20, driver);
            driver.quit();
            listingStockService.addAllListingsToHistory(listingHistories);
        }

//        return all timestamps before given timestamp
        if(from == null && to != null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateBefore(ticker, to);
        }
//        return all timestamps after given timestamp
        else if(from != null && to == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateAfter(ticker, from);
        }
//        return all timestamps between two timestamps
        else if(from != null && to != null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateBetween(ticker, from, to);
        }

        return listingHistories;
    }

    @Override
    public List<ListingFuture> getAllFutures(){
        return futureRepository.findAll();
    }
}

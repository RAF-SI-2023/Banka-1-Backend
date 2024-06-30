package rs.edu.raf.banka1.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.FutureMapper;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.dtos.ListingFutureDto;
import rs.edu.raf.banka1.repositories.FutureRepository;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.threads.FutureThread;

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
    private final ListingStockService listingStockService;
    private final DriverService driverService;
    private WebDriver driver;
    @Autowired
    public FuturesServiceImpl(FutureRepository futureRepository,
                              ListingHistoryRepository listingHistoryRepository,
                              FutureMapper futureMapper,
                              ListingStockService listingStockService,
                              DriverService driverService) {
        this.futureRepository = futureRepository;
        this.listingHistoryRepository = listingHistoryRepository;
        this.futureMapper = futureMapper;
        this.listingStockService = listingStockService;
        this.driverService = driverService;

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


    @Override
    public List<ListingFuture> fetchNFutures(int n) {

        driver = driverService.createNewDriver();

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

        WebElement tableDiv = driver.findElement(By.cssSelector("div.container.svelte-tx3nkj"));

        // Find all li elements within the parent div
        List<WebElement> tableRows = tableDiv.findElements(By.tagName("li"));

        String settlementDate = tableRows.get(1).findElement(By.className("value")).getText();
        String bid = tableRows.get(3).findElement(By.className("value")).getText();
        String lastPrice = tableRows.get(4).findElement(By.className("value")).getText();
        String volume = tableRows.get(6).findElement(By.className("value")).getText();
        String ask = tableRows.get(7).findElement(By.className("value")).getText();

        ListingFutureDto listingFutureDto = new ListingFutureDto();
        listingFutureDto.setHigh(Double.parseDouble(ask.replaceAll(",", "")));
        listingFutureDto.setLow(Double.parseDouble(bid.replaceAll(",", "")));
        listingFutureDto.setSettlementDate((int) LocalDate.parse(settlementDate).atStartOfDay(ZoneOffset.UTC).toEpochSecond());

        double volumeMultiplier = 1.0;
        if (volume.contains("k") || volume.contains("K")) volumeMultiplier = 1000.0;
        if (volume.contains("m") || volume.contains("M")) volumeMultiplier = 1000000.0;
        if (volume.contains("b") || volume.contains("B")) volumeMultiplier = 1000000000.0;
        volume = volume.replace("k", "").replace("K", "")
                .replace("m", "").replace("M", "")
                .replace("b", "").replace("B", "");
        volumeMultiplier *= Double.parseDouble(volume.replaceAll(",", "").replaceAll("-", "0"));
        listingFutureDto.setVolume((int) volumeMultiplier);
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
        if (!futureRepository.findAll().isEmpty()) return 0;
        return (int) futures.stream().filter(this::addFuture).count();
    }

    @Override
    public Boolean addFuture(ListingFuture future) {
        Optional<ListingFuture> optionalFuture = futureRepository.findByTicker(future.getTicker());
        if (optionalFuture.isPresent()) {
            var oldFuture = optionalFuture.get();
            oldFuture = futureMapper.updateFuture(oldFuture, future);
            futureRepository.save(oldFuture);
            return false;
        }
        else{
            futureRepository.save(future);
            return true;
        }
    }

    @Override
    public List<ListingHistory> fetchNFutureHistories(List<ListingFuture> listingFutures, int n) {
        driver = driverService.createNewDriver();
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
        WebElement table = driver.findElement(By.cssSelector("div.table-container.svelte-ewueuo"));
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
            singleHistory.setVolume(Integer.parseInt(cells.get(6).replaceAll(",", "").replaceAll("-", "0")));
            history.add(singleHistory);
        }
        return history;
    }
    @Scheduled(fixedDelay = 900000)
    public void runFetchBackground(){
        Thread thread = new Thread(new FutureThread(this, listingStockService));
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ListingFuture> findByTicker(String ticker) {
        return futureRepository.findByTicker(ticker);
    }
}

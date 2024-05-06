package rs.edu.raf.banka1.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
import rs.edu.raf.banka1.threads.OptionsThread;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import static rs.edu.raf.banka1.utils.Constants.maxFutureHistories;

@Service
public class FuturesServiceImpl implements FuturesService {
    private final Map<String, String> monthsCode = new HashMap<>();
    private final FutureRepository futureRepository;
    private final ListingHistoryRepository listingHistoryRepository;
    private final ListingStockService listingStockService;
    private WebDriver driver;
    @Autowired
    public FuturesServiceImpl(FutureRepository futureRepository,
                              ListingHistoryRepository listingHistoryRepository,
                              ListingStockService listingStockService) {
        this.futureRepository = futureRepository;
        this.listingHistoryRepository = listingHistoryRepository;
        this.listingStockService = listingStockService;
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
            // optionally fetch missing future histories
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

    @Override
    public Optional<ListingFuture> findByTicker(String ticker) {
        return futureRepository.findByTicker(ticker);
    }
}

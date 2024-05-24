package rs.edu.raf.banka1.services;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.repositories.FutureRepository;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class FuturesServiceImpl implements FuturesService {
    private final FutureRepository futureRepository;
    private final ListingHistoryRepository listingHistoryRepository;
    private WebDriver driver;
    @Autowired
    public FuturesServiceImpl(FutureRepository futureRepository,
                              ListingHistoryRepository listingHistoryRepository) {
        this.futureRepository = futureRepository;
        this.listingHistoryRepository = listingHistoryRepository;
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
    @Cacheable(value = "futures", key = "#ticker")
    public Optional<ListingFuture> findByTicker(String ticker) {
        return futureRepository.findByTicker(ticker);
    }
}

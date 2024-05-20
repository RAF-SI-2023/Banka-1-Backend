package rs.edu.raf.banka1.services;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.repositories.ForexRepository;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Setter
@NoArgsConstructor
public class ForexServiceImpl implements ForexService {
    @Autowired
    private ListingHistoryRepository listingHistoryRepository;
    @Autowired
    private ForexRepository forexRepository;

    @Override
    public List<ListingForex> getAllForexes() {
        return forexRepository.findAll();
    }

    @Override
    public ListingForex getForexByTicker(String ticker) {
        return forexRepository.findByTicker(ticker).orElse(null);
    }

    @Override
    public List<ListingHistory> getListingHistoriesByTimestamp(Long id, Integer from, Integer to) {
        List<ListingHistory> listingHistories = new ArrayList<>();
        ListingForex forex = forexRepository.findById(id).orElse(null);
        if(forex == null){
            return listingHistories;
        }

        String ticker = forex.getTicker();
        listingHistories = listingHistoryRepository.getListingHistoriesByTicker(ticker);
        if(listingHistories.isEmpty()) {
            // possibly try to fetch histories if none are present
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
    public Optional<ListingForex> findById(Long id) {
        return forexRepository.findById(id);
    }
}

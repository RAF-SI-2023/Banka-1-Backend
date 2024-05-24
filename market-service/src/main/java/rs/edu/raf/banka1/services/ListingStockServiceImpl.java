package rs.edu.raf.banka1.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.StockMapper;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.entities.Country;
import rs.edu.raf.banka1.model.entities.Exchange;
import rs.edu.raf.banka1.model.entities.Holiday;
import rs.edu.raf.banka1.repositories.*;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
@Service
public class ListingStockServiceImpl implements ListingStockService {
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private ListingHistoryRepository listingHistoryRepository;
    @Autowired
    private ExchangeRepository exchangeRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private HolidayRepository holidayRepository;

    //treba zbog testova Clock
    @Setter
    private Clock clock = Clock.systemDefaultZone();

    public ListingStockServiceImpl() {

    }

    @Override
    public List<ListingStock> getAllStocks(){
        return stockRepository.findAll();
    }

    @Override
    public Optional<ListingStock> findByTicker(String ticker) {
        return stockRepository.findByTicker(ticker);
    }

    @Override
    public Optional<ListingStock> findById(Long id) {
        return stockRepository.findById(id);
    }

    public List<ListingHistory> getListingHistoriesByTimestamp(String ticker, Integer from, Integer to) {
        List<ListingHistory> listingHistories = new ArrayList<>();
//        return all timestamps
        if(from == null && to == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTicker(ticker);
        }
//        return all timestamps before given timestamp
        else if(from == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateBefore(ticker, to);
        }
//        return all timestamps after given timestamp
        else if(to == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateAfter(ticker, from);
        }
//        return all timestamps between two timestamps
        else{
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateBetween(ticker, from, to);
        }

        return listingHistories;
    }

    @Override
    public String getWorkingTimeById(Long id) {
        Optional<ListingStock> optionalListingStock = stockRepository.findById(id);
        if (!optionalListingStock.isPresent())
            return "Stock not found";
        ListingStock listingStock = optionalListingStock.get();

        Exchange exchange = listingStock.getExchange();

        Optional<Country> optionalCountry = countryRepository.findById(exchange.getCountry().getId());
        if (!optionalCountry.isPresent())
            return "Country not found";
        Country country = optionalCountry.get();

        int timezoneOffsetInSeconds = country.getTimezoneOffset();
        int hoursOffset = timezoneOffsetInSeconds / 3600;
        ZoneOffset zoneOffset = ZoneOffset.ofHours(hoursOffset);
        ZoneId exchangeZoneId = ZoneId.ofOffset("UTC", zoneOffset);

        Date openTimeDate = country.getOpenTime();
        Date closeTimeDate = country.getCloseTime();

        LocalTime openingLocalTime = Instant.ofEpochMilli(openTimeDate.getTime()).atZone(exchangeZoneId).toLocalTime();
        LocalTime closingLocalTime = Instant.ofEpochMilli(closeTimeDate.getTime()).atZone(exchangeZoneId).toLocalTime();
        LocalDate today = LocalDate.now(exchangeZoneId);

        LocalDateTime openingTime = LocalDateTime.of(today, openingLocalTime);
        LocalDateTime closingTime = LocalDateTime.of(today, closingLocalTime);
        LocalDateTime now = LocalDateTime.now(exchangeZoneId);

        Optional<List<Holiday>> optionalHoliday = holidayRepository.findByCountryId(country.getId());
        for (Holiday holiday : optionalHoliday.get()) {
            LocalDate localDate = Instant.ofEpochMilli(holiday.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if(localDate.equals(now.toLocalDate())){
                return "CLOSED";
            }
        }

        if (now.isBefore(openingTime) || now.isAfter(closingTime)) {
            if (now.isAfter(closingTime) && now.isBefore(closingTime.plusHours(4))) {
                return "AFTER_HOURS";
            }
            return "CLOSED";
        }
        return "OPENED";
    }

    @Override
    public List<ListingHistory> getListingHistoriesByTimestamp(Long id, Integer from, Integer to) {
        List<ListingHistory> listingHistories = new ArrayList<>();
//        find stock in database
        ListingStock stock = stockRepository.findById(id).orElse(null);
        if(stock == null){
            return listingHistories;
        }
        String ticker = stock.getTicker();
        listingHistories = listingHistoryRepository.getListingHistoriesByTicker(ticker);
        if(listingHistories.isEmpty()) {
            // optionally fetch history for given ticker (fetching logic is in fetching service)
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

}

package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ListingHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListingHistoryRepository extends JpaRepository<ListingHistory, Long> {
    Optional<ListingHistory> findByTickerAndDate(String ticker, long date);
    List<ListingHistory> getListingHistoriesByTicker(String ticker);
    List<ListingHistory> getListingHistoriesByTickerAndDateBefore(String ticker, Integer date);
    List<ListingHistory> getListingHistoriesByTickerAndDateAfter(String ticker, Integer date);
    List<ListingHistory> getListingHistoriesByTickerAndDateBetween(String ticker, Integer from, Integer to);
}

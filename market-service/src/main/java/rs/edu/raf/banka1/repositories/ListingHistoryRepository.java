package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.banka1.model.ListingHistory;

import java.util.List;
import java.util.Optional;

public interface ListingHistoryRepository extends JpaRepository<ListingHistory, Long> {
    Optional<ListingHistory> findByTickerAndDate(String ticker, long date);
    List<ListingHistory> getListingHistoriesByListingId(Long id);
    List<ListingHistory> getListingHistoriesByListingIdAndDateBefore(Long id, Integer date);
    List<ListingHistory> getListingHistoriesByListingIdAndDateAfter(Long id, Integer date);
    List<ListingHistory> getListingHistoriesByListingIdAndDateBetween(Long id, Integer from, Integer to);
}

package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ListingHistory;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListingHistoryRepository extends JpaRepository<ListingHistory, Long> {
    Optional<ListingHistory> findByTickerAndDate(String ticker, long date);
    Optional<ListingHistory> findByTicker(String ticker);

}

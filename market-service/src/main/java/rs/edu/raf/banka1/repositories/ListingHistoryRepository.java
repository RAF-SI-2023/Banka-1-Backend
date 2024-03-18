package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.banka1.model.ListingHistoryModel;

import java.util.Optional;

public interface ListingHistoryRepository extends JpaRepository<ListingHistoryModel, Long> {
    Optional<ListingHistoryModel> findByTickerAndDate(String ticker, long date);
}

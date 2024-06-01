package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ListingForex;

import java.util.Optional;

@Repository
public interface ForexRepository extends JpaRepository<ListingForex, Long> {
    @Query("SELECT f FROM ListingForex f WHERE f.ticker = ?1")
    Optional<ListingForex> findByTicker(String ticker);
}

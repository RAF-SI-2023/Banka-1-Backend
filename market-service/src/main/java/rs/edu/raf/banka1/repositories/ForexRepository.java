package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ListingForex;

@Repository
public interface ForexRepository extends JpaRepository<ListingForex, Long> {
    boolean existsByBaseCurrencyAndQuoteCurrency(String baseCurrency, String quoteCurrency);
    ListingForex findByBaseCurrencyAndQuoteCurrency(String baseCurrency, String quoteCurrency);
}

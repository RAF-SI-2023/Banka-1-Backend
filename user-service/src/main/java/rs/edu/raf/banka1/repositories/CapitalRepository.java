package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.ListingType;

@Repository
public interface CapitalRepository extends JpaRepository<Capital, Long> {

    Capital getCapitalByCurrency_CurrencyCode(String curr);
    Capital getCapitalByListingIdAndListingType(Long listingId, ListingType listingType);
}

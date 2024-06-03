package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.MarginAccount;

import java.util.Optional;

@Repository
public interface MarginAccountRepository extends JpaRepository<MarginAccount, Long> {

    Optional<MarginAccount> findByCustomer_IdAndListingTypeAndCurrency_CurrencyCode(Long id, ListingType listingType, String currencyCode);
}

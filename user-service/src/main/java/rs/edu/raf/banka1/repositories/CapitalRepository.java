package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.ListingType;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface CapitalRepository extends JpaRepository<Capital, Long> {

    Optional<Capital> getCapitalByListingIdAndListingTypeAndBankAccount(Long listingId, ListingType listingType, BankAccount bankAccount);
    List<Capital> getCapitalsByListingTypeAndBankAccount(ListingType listingType, BankAccount bankAccount);
    List<Capital> getCapitalsByBankAccountAndListingType(BankAccount bankAccount, ListingType listingType);
//    Optional<Capital> getCapitalByBankAccount(BankAccount bankAccount);

    List<Capital> findByBankAccount_CompanyNullAndListingTypeAndPublicTotalGreaterThan(ListingType listingType, Double publicTotal);

    @Query("SELECT c FROM Capital c WHERE c.bankAccount.company IS NOT NULL AND c.publicTotal > 0")
    List<Capital> getAllPublicCapitals();

    List<Capital> getAllByPublicTotalGreaterThan(Double publicTotal);

    List<Capital> findByBankAccount_AccountNumber(String accountNumber);
}

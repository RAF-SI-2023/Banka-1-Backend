package rs.edu.raf.banka1.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.Loan;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>{
    Optional<Loan> findByAccountNumber(final String accountNumber);

    @Query("SELECT l FROM Loan l WHERE l.accountNumber IN " +
        "(SELECT ca.accountNumber FROM BankAccount ca WHERE ca.customer.userId = :userId )")
    List<Loan> findByUser(final Long userId);
}

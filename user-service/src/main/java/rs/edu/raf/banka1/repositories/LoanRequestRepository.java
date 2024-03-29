package rs.edu.raf.banka1.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.LoanRequest;
import rs.edu.raf.banka1.model.LoanRequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
    List<LoanRequest> findByAccountNumber(final String accountNumber);

    @Transactional
    @Modifying
    @Query("UPDATE LoanRequest lr SET lr.status = :loanRequestStatus WHERE lr.id = :loanRequestId")
    void changeStatusForLoan(final Long loanRequestId, final LoanRequestStatus loanRequestStatus);
}

package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Loan;
import rs.edu.raf.banka1.model.Payment;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.senderBankAccount.accountNumber = :accountNumber")
    List<Payment> findBySenderAccountNumber(String accountNumber);

}

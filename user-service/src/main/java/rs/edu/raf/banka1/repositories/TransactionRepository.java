package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.Transaction;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> getTransactionsByBankAccount_AccountNumber(String accountNumber);

    List<Transaction> getTransactionsByEmployee_UserId(Long id);

    List<Transaction> getTransactionsByMarketOrder_Id(Long marketOrder_id);

    @Query("SELECT sum(t.buy) from Transaction t where t.marketOrder.id = :orderId")
    Double getBuySumByOrderId(Long orderId);

    @Query("SELECT sum(t.sell) from Transaction t where t.marketOrder.id = :orderId")
    Double getSellSumByOrderId(Long orderId);

}

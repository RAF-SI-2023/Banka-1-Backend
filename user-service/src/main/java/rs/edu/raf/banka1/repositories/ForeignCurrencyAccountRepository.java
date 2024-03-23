package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForeignCurrencyAccountRepository extends JpaRepository<ForeignCurrencyAccount, Long> {

    List<ForeignCurrencyAccount> findByOwnerId(Long ownerId);

//    List<ForeignCurrencyAccount> findForeignCurrencyAccountByUser(User user);

    Optional<ForeignCurrencyAccount> findForeignCurrencyAccountByAccountNumber(String accountNumber);

    List<ForeignCurrencyAccount> findByCreatedByAgentId(Long agentId);
}

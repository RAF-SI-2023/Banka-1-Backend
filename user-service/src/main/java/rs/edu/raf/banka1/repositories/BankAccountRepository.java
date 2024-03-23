package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Company;
import rs.edu.raf.banka1.model.User;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByUser(User user);
    List<BankAccount> findByCompany(Company company);
    List<BankAccount> findByCreatedByAgentId(Long agentId);
    List<BankAccount> findByAccountNumber(String accountNumber);
}

package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Company;
import rs.edu.raf.banka1.model.Customer;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByCustomer(Customer customer);
    List<BankAccount> findByCompany(Company company);
    List<BankAccount> findByCreatedByAgentId(Long agentId);
    Optional<BankAccount> findBankAccountByAccountNumber(String accountNumber);
}

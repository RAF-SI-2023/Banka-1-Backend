package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.CurrentAccount;

import java.util.List;

@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long>{
    List<CurrentAccount> findByOwnerId(Long ownerId);
    List<CurrentAccount> findByCreatedByAgentId(Long agentId);
}

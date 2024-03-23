package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.BusinessAccount;

import java.util.List;

@Repository
public interface BusinessAccountRepository extends JpaRepository<BusinessAccount, Long>{
    List<BusinessAccount> findByOwnerId(Long ownerId);
    List<BusinessAccount> findByCreatedByAgentId(Long agentId);
}

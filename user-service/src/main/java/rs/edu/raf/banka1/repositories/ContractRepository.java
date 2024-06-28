package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.banka1.model.Contract;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {


    @Transactional
    @Modifying
    @Query("update Contract c set c.comment = ?1 where c.id = ?2")
    void updateCommentById(String comment, Long id);

    @Query("select c from Contract c where c.buyer.accountNumber = ?1 or c.seller.accountNumber = ?1")
    List<Contract> findAllCustomerContracts(String accountNumber);

    @Transactional
    @Modifying
    @Query("update Contract c set c.sellerApproval = true where c.id = ?1")
    void acceptContract(Long id);

    @Transactional
    @Modifying
    @Query("update Contract c set c.bankApproval = true where c.id = ?1")
    void approveContract(Long id);

    @Transactional
    @Modifying
    @Query("update Contract c set c.bankApproval = false where c.id = ?1")
    void denyContract(Long id);

}

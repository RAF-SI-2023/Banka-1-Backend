package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.Employee;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findCustomerByActivationToken(String token);
    Optional<Customer> findCustomerByEmail(String email);
    Optional<Customer> findByUserId(Long userId);
    Optional<Customer> findByResetPasswordToken(String token);

}

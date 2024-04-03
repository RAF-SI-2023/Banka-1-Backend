package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.banka1.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}

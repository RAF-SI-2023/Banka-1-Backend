package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.banka1.model.Capital;

public interface CapitalRepository extends JpaRepository<Capital, Long> {
}

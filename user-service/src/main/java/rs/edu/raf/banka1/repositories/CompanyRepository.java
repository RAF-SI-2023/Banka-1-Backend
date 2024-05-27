package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.Company;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findCompaniesByPibContainingIgnoreCase(String pib);

    List<Company> findCompaniesByIdNumberContainingIgnoreCase(String idNumber);

    List<Company> findCompaniesByCompanyNameContainingIgnoreCase(String name);



}

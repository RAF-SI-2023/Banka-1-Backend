package rs.edu.raf.banka1.exceptions;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(String companyPib) {
        super("Company with pib: " + companyPib + " couldn't be found.");
    }
}

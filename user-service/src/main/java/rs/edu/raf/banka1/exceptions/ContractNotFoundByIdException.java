package rs.edu.raf.banka1.exceptions;

public class ContractNotFoundByIdException extends RuntimeException {
    public ContractNotFoundByIdException(Long contractId) {
        super("Contract with id " + contractId + " not found");
    }
}

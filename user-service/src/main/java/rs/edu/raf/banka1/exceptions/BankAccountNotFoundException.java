package rs.edu.raf.banka1.exceptions;

public class BankAccountNotFoundException extends RuntimeException{
    public BankAccountNotFoundException() {
        super("Bank account couldn't be found.");
    }
}

package rs.edu.raf.banka1.exceptions;

public class BankAccountNotFoundException extends NotFoundException{
    public BankAccountNotFoundException() {
        super("Bank account couldn't be found.");
    }
}

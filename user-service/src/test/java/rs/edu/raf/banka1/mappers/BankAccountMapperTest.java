package rs.edu.raf.banka1.mappers;
import org.junit.jupiter.api.Test;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.mapper.BankAccountMapper;
import rs.edu.raf.banka1.model.AccountType;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BankAccountMapperTest {

    private final BankAccountMapper bankAccountMapper = new BankAccountMapper();

    @Test
    void testToDto() {
        // Arrange
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountType(AccountType.CURRENT);
        bankAccount.setAccountNumber("123456789");
        bankAccount.setAccountName("Test Account");
        bankAccount.setAccountStatus(true); // Active
        bankAccount.setCurrency(new Currency());
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        // Act
        BankAccountDto bankAccountDto = bankAccountMapper.toDto(bankAccount);

        // Assert
        assertEquals(bankAccount.getAccountType().name(), bankAccountDto.getAccountType());
        assertEquals(bankAccount.getAccountNumber(), bankAccountDto.getAccountNumber());
        assertEquals(bankAccount.getAccountName(), bankAccountDto.getAccountName());
        assertEquals("ACTIVE", bankAccountDto.getAccountStatus());
        assertEquals(bankAccount.getCurrency().getCurrencyCode(), bankAccountDto.getCurrency());
        assertEquals(bankAccount.getBalance(), bankAccountDto.getBalance());
        assertEquals(bankAccount.getAvailableBalance(), bankAccountDto.getAvailableBalance());
    }
}

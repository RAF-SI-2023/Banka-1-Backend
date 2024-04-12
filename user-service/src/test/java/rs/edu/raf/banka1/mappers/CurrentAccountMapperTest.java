package rs.edu.raf.banka1.mappers;

import org.junit.jupiter.api.Test;
import rs.edu.raf.banka1.dtos.CurrentAccountDto;
import rs.edu.raf.banka1.model.CurrentAccount;
import rs.edu.raf.banka1.mapper.CurrentAccountMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrentAccountMapperTest {

    private final CurrentAccountMapper currentAccountMapper = new CurrentAccountMapper();

    @Test
    void testToDto() {
        // Arrange
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(1L);
        currentAccount.setAccountNumber("ACC123");
        currentAccount.setOwnerId(2L);
        currentAccount.setBalance(1000.0);
        currentAccount.setAvailableBalance(900.0);
        currentAccount.setCreatedByAgentId(3L);
        currentAccount.setCreationDate(1649155200000L); // Timestamp for 2022-04-04 00:00:00
        currentAccount.setExpirationDate(1670256000000L); // Timestamp for 2022-12-04 00:00:00
        currentAccount.setCurrency("EUR");
        currentAccount.setAccountStatus("ACTIVE");
        currentAccount.setSubtypeOfAccount("Personal");
        currentAccount.setAccountMaintenance(50.0);

        // Act
        CurrentAccountDto currentAccountDto = currentAccountMapper.toDto(currentAccount);

        // Assert
        assertEquals(currentAccount.getId(), currentAccountDto.getId());
        assertEquals(currentAccount.getAccountNumber(), currentAccountDto.getAccountNumber());
        assertEquals(currentAccount.getOwnerId(), currentAccountDto.getOwnerId());
        assertEquals(currentAccount.getBalance(), currentAccountDto.getBalance());
        assertEquals(currentAccount.getAvailableBalance(), currentAccountDto.getAvailableBalance());
        assertEquals(currentAccount.getCreatedByAgentId(), currentAccountDto.getCreatedByAgentId());
        assertEquals(currentAccount.getCreationDate(), currentAccountDto.getCreationDate());
        assertEquals(currentAccount.getExpirationDate(), currentAccountDto.getExpirationDate());
        assertEquals(currentAccount.getCurrency(), currentAccountDto.getCurrency());
        assertEquals(currentAccount.getAccountStatus(), currentAccountDto.getAccountStatus());
        assertEquals(currentAccount.getSubtypeOfAccount(), currentAccountDto.getSubtypeOfAccount());
        assertEquals(currentAccount.getAccountMaintenance(), currentAccountDto.getAccountMaintenance());
    }
}

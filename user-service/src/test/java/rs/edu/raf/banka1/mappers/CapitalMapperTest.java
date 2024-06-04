package rs.edu.raf.banka1.mappers;
import org.junit.jupiter.api.Test;
import rs.edu.raf.banka1.dtos.CapitalDto;
import rs.edu.raf.banka1.dtos.CapitalProfitDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.model.ListingType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CapitalMapperTest {

    private final CapitalMapper capitalMapper = new CapitalMapper();

    @Test
    void testCapitalToCapitalDto() {
        // Arrange
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234567890");
        Currency currency = new Currency();
        Capital capital = new Capital();
        capital.setListingId(1L);
        capital.setListingType(ListingType.STOCK);
        capital.setTotal(1000.0);
        capital.setReserved(200.0);
        capital.setBankAccount(bankAccount);

        // Act
        CapitalDto capitalDto = capitalMapper.capitalToCapitalDto(capital);

        // Assert
        assertEquals(capital.getListingId(), capitalDto.getListingId());
        assertEquals(capital.getListingType(), capitalDto.getListingType());
        assertEquals(capital.getTotal(), capitalDto.getTotal());
        assertEquals(capital.getReserved(), capitalDto.getReserved());
        assertEquals(bankAccount.getAccountNumber(), capitalDto.getBankAccountNumber());
    }

    @Test
    void testCapitalToCapitalProfitDto() {
        // Arrange
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234567890");
        Currency currency = new Currency();
        Capital capital = new Capital();
        capital.setListingId(1L);
        capital.setListingType(ListingType.STOCK);
        capital.setTotal(1000.0);
        capital.setReserved(200.0);
        capital.setBankAccount(bankAccount);
        Double price = 10.0;

        // Act
        CapitalProfitDto capitalProfitDto = capitalMapper.capitalToCapitalProfitDto(capital, price);

        // Assert
        assertEquals(capital.getListingId(), capitalProfitDto.getListingId());
        assertEquals(capital.getListingType(), capitalProfitDto.getListingType());
        assertEquals(capital.getTotal(), capitalProfitDto.getTotal());
        assertEquals(capital.getReserved(), capitalProfitDto.getReserved());
        assertEquals(bankAccount.getAccountNumber(), capitalProfitDto.getBankAccountNumber());
        assertEquals((capital.getTotal() - capital.getReserved()) * price, capitalProfitDto.getTotalPrice());
    }
}

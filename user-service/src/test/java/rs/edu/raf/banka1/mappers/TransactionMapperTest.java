package rs.edu.raf.banka1.mappers;

import org.junit.jupiter.api.Test;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.mapper.BankAccountMapper;
import rs.edu.raf.banka1.mapper.EmployeeMapper;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.mapper.TransactionMapper;
import rs.edu.raf.banka1.model.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionMapperTest {

    @Test
    void testTransactionToTransactionDto() {
        // Arrange
        OrderMapper orderMapper = mock(OrderMapper.class);
        EmployeeMapper employeeMapper = mock(EmployeeMapper.class);
        BankAccountMapper bankAccountMapper = mock(BankAccountMapper.class);

        TransactionMapper mapper = new TransactionMapper(orderMapper, employeeMapper, bankAccountMapper);

        Transaction transaction = new Transaction();
        transaction.setDateTime(Instant.now().toEpochMilli());
        transaction.setDescription("Test transaction");
        transaction.setCurrency(new Currency());
        transaction.setBuy(100.0);
        transaction.setSell(90.0);
        transaction.setReserved(10.0);
        transaction.setReserveUsed(5.0);

        BankAccount bankAccount = mock(BankAccount.class);
        MarketOrder marketOrder = mock(MarketOrder.class);
        Employee employee = mock(Employee.class);

        // Mockovanje EmployeeMapper-a
        EmployeeDto employeeDto = new EmployeeDto();
        when(employeeMapper.employeeToEmployeeDto(transaction.getEmployee())).thenReturn(employeeDto);

        when(bankAccountMapper.toDto(transaction.getBankAccount())).thenReturn(mock(BankAccountDto.class));
        when(orderMapper.marketOrderToOrderDto(transaction.getMarketOrder())).thenReturn(mock(OrderDto.class));

        // Act
        TransactionDto transactionDto = mapper.transactionToTransactionDto(transaction);

        // Assert
        assertEquals(transaction.getDateTime(), transactionDto.getDateTime());
        assertEquals(transaction.getDescription(), transactionDto.getDescription());
        assertEquals(transaction.getCurrency(), transactionDto.getCurrency());
        assertEquals(transaction.getBuy(), transactionDto.getBuy());
        assertEquals(transaction.getSell(), transactionDto.getSell());
        assertEquals(transaction.getReserved(), transactionDto.getReserved());
        assertEquals(transaction.getReserveUsed(), transactionDto.getReserveUsed());
    }
}

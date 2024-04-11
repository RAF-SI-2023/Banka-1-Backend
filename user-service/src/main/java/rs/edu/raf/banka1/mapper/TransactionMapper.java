package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.model.Transaction;

@Component
public class TransactionMapper {
    private final OrderMapper orderMapper;
    private final EmployeeMapper employeeMapper;
    private final BankAccountMapper bankAccountMapper;

    public TransactionMapper(OrderMapper orderMapper, EmployeeMapper employeeMapper, BankAccountMapper bankAccountMapper) {
        this.orderMapper = orderMapper;
        this.employeeMapper = employeeMapper;
        this.bankAccountMapper = bankAccountMapper;
    }

    public TransactionDto transactionToTransactionDto(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setBankAccount(bankAccountMapper.toDto(transaction.getBankAccount()));
        transactionDto.setDateTime(transaction.getDateTime());
        transactionDto.setMarketOrder(orderMapper.marketOrderToOrderDto(transaction.getMarketOrder()));
        transactionDto.setEmployee(employeeMapper.employeeToEmployeeDto(transaction.getEmployee()));
        transactionDto.setDescription(transaction.getDescription());
        transactionDto.setCurrency(transaction.getCurrency());
        transactionDto.setBuy(transaction.getBuy());
        transactionDto.setSell(transaction.getSell());
        transactionDto.setReserved(transaction.getReserved());
        transactionDto.setReserveUsed(transaction.getReserveUsed());
        return transactionDto;
    }
}

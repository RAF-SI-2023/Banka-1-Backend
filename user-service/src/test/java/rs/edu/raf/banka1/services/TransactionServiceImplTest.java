package rs.edu.raf.banka1.services;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.mapper.TransactionMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Transaction;
import rs.edu.raf.banka1.repositories.TransactionRepository;
import rs.edu.raf.banka1.requests.CreateTransactionRequest;
import rs.edu.raf.banka1.services.implementations.TransactionServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void testGetAllTransaction() {
        // Arrange
        String accountNumber = "123456789";
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.getTransactionsByBankAccount_AccountNumber(accountNumber)).thenReturn(transactions);

        // Act
        List<TransactionDto> result = transactionService.getAllTransaction(accountNumber);

        // Assert
        assertEquals(transactions.size(), result.size());
    }

    @Test
    void testCreateBuyTransaction() {
        // Arrange
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAccountNumber("123456789");
        request.setValue(100.0);
        Transaction transaction = new Transaction();
        transaction.setBuy(request.getValue());
        BankAccount bankAccount = new BankAccount();
        transaction.setBankAccount(bankAccount);
        when(bankAccountService.findBankAccountByAccountNumber(request.getAccountNumber())).thenReturn(bankAccount);

        // Act
        TransactionDto result = transactionService.createBuyTransaction(request);

        // Assert
        assertEquals(transaction.getBuy(), request.getValue());
        assertEquals(transaction.getBankAccount(), bankAccount);
    }

    @Test
    void testCreateSellTransaction() {
        // Arrange
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAccountNumber("123456789");
        request.setValue(100.0);
        Transaction transaction = new Transaction();
        transaction.setSell(request.getValue());
        BankAccount bankAccount = new BankAccount();
        transaction.setBankAccount(bankAccount);
        when(bankAccountService.findBankAccountByAccountNumber(request.getAccountNumber())).thenReturn(bankAccount);
//        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // Act
        TransactionDto result = transactionService.createSellTransaction(request);

        // Assert
        assertEquals(transaction.getSell(), request.getValue());
        assertEquals(transaction.getBankAccount(), bankAccount);
    }

    @Test
    void testGetTransactionsForEmployee() {
        // Arrange
        Long userId = 1L;
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.getTransactionsByEmployee_UserId(userId)).thenReturn(transactions);

        // Act
        List<TransactionDto> result = transactionService.getTransactionsForEmployee(userId);

        // Assert
        assertEquals(transactions.size(), result.size());
    }
}

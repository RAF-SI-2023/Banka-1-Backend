package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.TransferRepository;
import rs.edu.raf.banka1.requests.CreateTransferRequest;
import rs.edu.raf.banka1.services.implementations.TransferServiceImpl;
import rs.edu.raf.banka1.model.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferServiceImplTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    public void testCreateTransfer_Successful() {
        // Mocking the repository to return some bank accounts
        BankAccount senderAccount = new BankAccount();
        senderAccount.setId(1L);
        senderAccount.setAccountNumber("1234567890");

        BankAccount recipientAccount = new BankAccount();
        recipientAccount.setId(2L);
        recipientAccount.setAccountNumber("0987654321");

        when(bankAccountRepository.findBankAccountByAccountNumber("1234567890")).thenReturn(Optional.of(senderAccount));
        when(bankAccountRepository.findBankAccountByAccountNumber("0987654321")).thenReturn(Optional.of(recipientAccount));

        CreateTransferRequest request = new CreateTransferRequest();
        request.setSenderAccountNumber("1234567890");
        request.setRecipientAccountNumber("0987654321");
        request.setAmount(100.00);

        Long transferId = transferService.createTransfer(request);

        assertEquals(1L, transferId.longValue()); // Assuming the saved transfer ID is 1
        verify(transferRepository, times(1)).save(any(Transfer.class)); // Verify that save method was called
    }

}
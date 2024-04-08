package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.TransferDto;
import rs.edu.raf.banka1.mapper.TransferMapper;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.TransferRepository;
import rs.edu.raf.banka1.requests.CreateTransferRequest;
import rs.edu.raf.banka1.services.implementations.TransferServiceImpl;
import rs.edu.raf.banka1.model.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferServiceImplTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private TransferMapper transferMapper;
    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    public void testCreateTransferSuccessful() {
        BankAccount senderAccount = new BankAccount();
        BankAccount recipientAccount = new BankAccount();

        when(bankAccountRepository.findBankAccountByAccountNumber("123")).thenReturn(Optional.of(senderAccount));
        when(bankAccountRepository.findBankAccountByAccountNumber("456")).thenReturn(Optional.of(recipientAccount));

        CreateTransferRequest request = new CreateTransferRequest();
        request.setSenderAccountNumber("123");
        request.setRecipientAccountNumber("456");
        request.setAmount(100.00);

        Long result = transferService.createTransfer(request);

        assertNotEquals(-1L, result);
        verify(transferRepository, times(1)).save(any(Transfer.class));
    }

    @Test
    public void testCreateTransferAccountsNotFound() {
        when(bankAccountRepository.findBankAccountByAccountNumber("1234567890")).thenReturn(Optional.empty());
        when(bankAccountRepository.findBankAccountByAccountNumber("0987654321")).thenReturn(Optional.empty());

        CreateTransferRequest request = new CreateTransferRequest();
        request.setSenderAccountNumber("1234567890");
        request.setRecipientAccountNumber("0987654321");
        request.setAmount(100.00);

        Long transferId = transferService.createTransfer(request);

        assertEquals(-1L, transferId.longValue());
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    public void getTranferByIdTestTransferValid(){
        Long transferId = 1L;
        Transfer transfer = new Transfer();
        transfer.setId(transferId);

        TransferDto expectedDto = new TransferDto();
        expectedDto.setId(transferId);

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));
        when(transferMapper.transferToTransferDto(transfer)).thenReturn(expectedDto);

        TransferDto resultDto = transferService.getTransferById(transferId);
        assertNotNull(resultDto);
        assertEquals(expectedDto.getId(),resultDto.getId());
    }

    @Test
    public void testGetTransferById_NonExistingTransfer() {
        Long invalidPaymentId = 100L;
        when(transferRepository.findById(invalidPaymentId)).thenReturn(Optional.empty());

        TransferDto resultDto  = transferService.getTransferById(invalidPaymentId);

        assertNull(resultDto);
    }

    @Test
    public void testGetAllTransfersForAccountNumberValidAccountWithTransfers() {
        String accountNumber = "123456789";
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);

        Transfer transfer1 = new Transfer();
        transfer1.setId(1l);
        Transfer transfer2 = new Transfer();
        transfer2.setId(2l);
        List<Transfer> transfers = new ArrayList<>();
        transfers.add(transfer1);
        transfers.add(transfer2);
        bankAccount.setTransfers(transfers);

        when(bankAccountRepository.findBankAccountByAccountNumber("123456789")).thenReturn(Optional.of(bankAccount));
        when(transferMapper.transferToTransferDto(any(Transfer.class)))
                .thenAnswer(invocation->{
                    Transfer transfer = invocation.getArgument(0);
                    return new TransferDto();
                });


        List<TransferDto> result = transferService.getAllTransfersForAccountNumber("123456789");


        assertNotNull(result);
        assertEquals(2, result.size());

    }

    @Test
    public void testGetAllTransfersForAccountNumberInvalidAccount() {
        String invalidAccountNumber = "987654321";
        when(bankAccountRepository.findBankAccountByAccountNumber("987654321"))
                .thenReturn(Optional.empty());

        List<TransferDto> result = transferService.getAllTransfersForAccountNumber("nonExistingAccount");

        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllTransfersForAccountNumberWithNoTransfers() {
        BankAccount emptyBankAccount = new BankAccount();
        emptyBankAccount.setTransfers(new ArrayList<>());
        when(bankAccountRepository.findBankAccountByAccountNumber("123456")).thenReturn(Optional.of(emptyBankAccount));

        List<TransferDto> result = transferService.getAllTransfersForAccountNumber("emptyAccount");

        assertEquals(0, result.size());
    }
}
package rs.edu.raf.banka1.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import rs.edu.raf.banka1.dtos.TransferDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.TransactionStatus;
import rs.edu.raf.banka1.model.Transfer;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferMapperTest {

    private TransferMapper transferMapper = new TransferMapper();

    @Test
    void transferToTransferDto() {
        Transfer transfer = new Transfer();

        transfer.setAmount(1.0);
        transfer.setId(1L);
        transfer.setStatus(TransactionStatus.PROCESSING);
        transfer.setCommission(2.0);
        transfer.setConvertedAmount(3.0);
        transfer.setExchangeRate(4.0);

        TransferDto res = transferMapper.transferToTransferDto(transfer);

        assertEquals(res.getAmount(), 1.0);
        assertEquals(res.getId(), 1L);
        assertEquals(res.getStatus(), TransactionStatus.PROCESSING);
        assertEquals(res.getCommission(), 2.0);
        assertEquals(res.getConvertedAmount(), 3.0);
        assertEquals(res.getExchangeRate(), 4.0);
    }
}
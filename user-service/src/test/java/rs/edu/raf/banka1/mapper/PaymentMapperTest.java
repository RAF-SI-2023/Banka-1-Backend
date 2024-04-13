package rs.edu.raf.banka1.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.PaymentDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.Payment;
import rs.edu.raf.banka1.model.TransactionStatus;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentMapperTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @InjectMocks
    private PaymentMapper paymentMapper;

    @Test
    void paymentToPaymentDto() {
        Customer customer = new Customer();
        customer.setFirstName("name");
        customer.setLastName("lastname");
        BankAccount bankAccount = new BankAccount();
        bankAccount.setCustomer(customer);
        bankAccount.setAccountNumber("1234");

        Payment payment = new Payment();
        payment.setSenderBankAccount(bankAccount);
        payment.setId(1L);
        payment.setAmount(2.0);

        PaymentDto dto = paymentMapper.paymentToPaymentDto(payment);

        assertEquals(customer.getFirstName() + " " + customer.getLastName(), dto.getSenderAccountOwnerName());
        assertEquals(bankAccount.getAccountNumber(), dto.getSenderAccountNumber());
        assertEquals(payment.getId(), dto.getId());
        assertEquals(payment.getAmount(), dto.getAmount());
    }

    @Test
    void createPaymentRequestToPayment() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234");

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setAmount(1.0);
        request.setSenderAccountNumber("1234");

        when(bankAccountRepository.findBankAccountByAccountNumber("1234")).thenReturn(Optional.of(bankAccount));

        Payment res = paymentMapper.createPaymentRequestToPayment(request);

        assertEquals(res.getSenderBankAccount(), bankAccount);
        assertEquals(res.getAmount(), request.getAmount());
        assertEquals(res.getStatus(), TransactionStatus.PROCESSING);
        assertEquals(res.getChannel(), "web");

    }
}
package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.mapper.PaymentMapper;
import rs.edu.raf.banka1.dtos.PaymentDto;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.repositories.PaymentRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRequest;
import rs.edu.raf.banka1.services.implementations.PaymentServiceImpl;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmailService emailService;
    @Mock
    private JwtUtil jwtUtil;
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp(){
        PaymentMapper paymentMapper = new PaymentMapper(bankAccountRepository);
        paymentService = new PaymentServiceImpl(paymentRepository,bankAccountRepository,customerRepository, paymentMapper,emailService,jwtUtil);
    }

    @Test
    public void createNewPaymentTestSuccessful(){
        String accountNumber = "123456789";
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setCustomer(new Customer());

        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setPaymentCode("123");
        createPaymentRequest.setReferenceNumber("99");
        createPaymentRequest.setModel("1");
        createPaymentRequest.setPaymentPurpose("Test");
        createPaymentRequest.setAmount(100.0);
        createPaymentRequest.setRecipientAccountNumber("1111");
        createPaymentRequest.setSenderAccountNumber("0000");
        createPaymentRequest.setRecipientName("Keri");

        Payment payment = new Payment();
        payment.setId(1L);

        when(bankAccountRepository.findBankAccountByAccountNumber(accountNumber)).thenReturn(Optional.of(bankAccount));
        when(paymentRepository.save(payment)).thenReturn(payment);

        Long paymentId = paymentService.createPayment(createPaymentRequest);

        Assertions.assertNotNull(paymentId);
//        assertEquals(1L, paymentId);
    }

    @Test
    public void createNewPaymentTestFail() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setSenderAccountNumber("InvalidAccount123");
        request.setRecipientName("RecipientName");

        Long paymentId = paymentService.createPayment(request);

        assertEquals(-1L, paymentId);
    }

    @Test
    public void processPaymentTestSuccessful() {
        Currency currency = new Currency();
        currency.setId(1L);

        BankAccount senderAccount = new BankAccount();
        senderAccount.setAvailableBalance(1000.0);
        senderAccount.setBalance(1000.0);
        senderAccount.setCurrency(currency);

        BankAccount recipientAccount = new BankAccount();
        recipientAccount.setAvailableBalance(0.0);
        recipientAccount.setBalance(0.0);
        recipientAccount.setCurrency(currency);

        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(TransactionStatus.PROCESSING);
        payment.setSenderBankAccount(senderAccount);
        payment.setAmount(100.0);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(bankAccountRepository.findBankAccountByAccountNumber(payment.getRecipientAccountNumber()))
                .thenReturn(Optional.of(recipientAccount));

        paymentService.processPayment(paymentId);

        assertEquals(TransactionStatus.COMPLETE, payment.getStatus());
        assertEquals(898.0, senderAccount.getAvailableBalance()); //  commission
        assertEquals(898.0, senderAccount.getBalance()); //  commission

        assertEquals(100.0, recipientAccount.getAvailableBalance());
        assertEquals(100.0, recipientAccount.getBalance());

        verify(bankAccountRepository, times(2)).save(any(BankAccount.class));
        verify(paymentRepository).save(payment);
    }

    @Test
    public void processPaymentTestDeniedInsufficientBalance() {
        // Set up payment, sender account, and recipient account with conditions that result in denial
        Currency currency = new Currency();
        currency.setId(1L);

        BankAccount senderAccount = new BankAccount();
        senderAccount.setAvailableBalance(100.0);
        senderAccount.setCurrency(currency);

        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(TransactionStatus.PROCESSING);
        payment.setSenderBankAccount(senderAccount);
        payment.setAmount(150.0);

        BankAccount recipientAccount = new BankAccount();
        recipientAccount.setCurrency(currency);
        recipientAccount.setAvailableBalance(100.0);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(bankAccountRepository.findBankAccountByAccountNumber(payment.getRecipientAccountNumber()))
                .thenReturn(Optional.of(recipientAccount));

        paymentService.processPayment(paymentId);

        assertEquals(TransactionStatus.DENIED, payment.getStatus());
        assertEquals(100.0, senderAccount.getAvailableBalance()); // Sender balance remains unchanged
        assertEquals(100.0, recipientAccount.getAvailableBalance()); // Recipient balance remains unchanged
        verify(bankAccountRepository, never()).save(any(BankAccount.class)); // No account saves should occur
        verify(paymentRepository).save(payment);
    }

    @Test
    public void processPaymentTestNonExistentPayment() {
        Long nonExistentPaymentId = 100L;
        when(paymentRepository.findById(nonExistentPaymentId)).thenReturn(Optional.empty());

        paymentService.processPayment(nonExistentPaymentId);

        // No changes should be made since the payment does not exist
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    public void getAllPaymentsForAccNumTestValidAccNum() {
        String accountNumber = "123456789";
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setCustomer(new Customer());

        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setAmount(100.0);
        payment1.setStatus(TransactionStatus.COMPLETE);
        payment1.setSenderBankAccount(bankAccount);

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setAmount(1100.0);
        payment2.setStatus(TransactionStatus.PROCESSING);
        payment2.setSenderBankAccount(bankAccount);

        List<Payment> payments = new ArrayList<>();
        payments.add(payment1);
        payments.add(payment2);
        bankAccount.setPayments(payments);

        when(bankAccountRepository.findBankAccountByAccountNumber(accountNumber))
                .thenReturn(Optional.of(bankAccount));

        List<PaymentDto> paymentDtos = paymentService.getAllPaymentsForAccountNumber(accountNumber);

        Assertions.assertNotNull(paymentDtos);
        assertEquals(payments.size(), paymentDtos.size());
    }

    @Test
    public void fetAllPaymentsForAccNumTestInvalidAccNum() {
        String invalidAccountNumber = "987654321";
        when(bankAccountRepository.findBankAccountByAccountNumber(invalidAccountNumber))
                .thenReturn(Optional.empty());

        List<PaymentDto> paymentDtos = paymentService.getAllPaymentsForAccountNumber(invalidAccountNumber);

        Assertions.assertNotNull(paymentDtos);
        assertTrue(paymentDtos.isEmpty());
    }

    @Test
    public void getPaymentByIdTestPaymentValid() {
        String accountNumber = "123456789";
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setCustomer(new Customer());

        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setSenderBankAccount(bankAccount);

        PaymentDto expectedDto = new PaymentDto();
        expectedDto.setId(paymentId);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        PaymentDto resultDto = paymentService.getPaymentById(paymentId);

        Assertions.assertNotNull(resultDto);
        assertEquals(expectedDto.getId(), resultDto.getId());
    }

    @Test
    public void getPaymentByIdTestPaymentInvalid() {
        Long nonExistentPaymentId = 100L;
        when(paymentRepository.findById(nonExistentPaymentId)).thenReturn(Optional.empty());

        PaymentDto resultDto = paymentService.getPaymentById(nonExistentPaymentId);

        assertNull(resultDto);
    }
    @Test
    public void sendSingleUseCodeTestCustomerExists() {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setUserId(customerId);
        customer.setEmail("test@example.com");

        String singleUseCode = "123456"; // Mocked single use code

        when(customerRepository.findByUserId(customerId)).thenReturn(Optional.of(customer));
        when(jwtUtil.generateSingleUseCode(customer.getEmail())).thenReturn(singleUseCode);
        when(emailService.sendEmail(eq(customer.getEmail()), anyString(), anyString())).thenReturn(true);

        boolean result = paymentService.sendSingleUseCode(customerId);

        assertTrue(result);
        assertEquals(singleUseCode, customer.getSingleUseCode());
        verify(customerRepository).save(customer);
        verify(emailService).sendEmail(customer.getEmail(), "RAF Banka - Single use token",
                "Your single use code:\n" + singleUseCode);
    }

    @Test
    public void testSendSingleUseCode_CustomerNotExists() {
        Long nonExistentCustomerId = 100L;
        when(customerRepository.findByUserId(nonExistentCustomerId)).thenReturn(Optional.empty());

        boolean result = paymentService.sendSingleUseCode(nonExistentCustomerId);

        assertFalse(result);
    }

    @Test
    public void testValidatePayment_ValidRequest() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setSenderAccountNumber("123456789");
        request.setSingleUseCode("123456");

        BankAccount bankAccount = new BankAccount();
        bankAccount.setCustomer(new Customer());
        bankAccount.getCustomer().setSingleUseCode(request.getSingleUseCode()); // Set single use code

        when(bankAccountRepository.findBankAccountByAccountNumber(request.getSenderAccountNumber()))
                .thenReturn(Optional.of(bankAccount));
        when(jwtUtil.isTokenExpired(request.getSingleUseCode())).thenReturn(false);

        boolean result = paymentService.validatePayment(request);


        assertTrue(result);
        assertNull(bankAccount.getCustomer().getSingleUseCode()); // Single use code should be cleared
        verify(customerRepository).save(bankAccount.getCustomer());
    }

    @Test
    public void testValidatePayment_InvalidRequest() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setSenderAccountNumber("123456789");
        request.setSingleUseCode("123456"); // Mocked single use code

        BankAccount bankAccount = new BankAccount();
        bankAccount.setCustomer(new Customer());
        bankAccount.getCustomer().setSingleUseCode("654321"); // Different single use code

        when(bankAccountRepository.findBankAccountByAccountNumber(request.getSenderAccountNumber()))
                .thenReturn(Optional.of(bankAccount));
        when(jwtUtil.isTokenExpired(request.getSingleUseCode())).thenReturn(false);

        boolean result = paymentService.validatePayment(request);

        assertFalse(result);
        assertEquals("654321", bankAccount.getCustomer().getSingleUseCode()); // Single use code should not be cleared
        verify(customerRepository, never()).save(any(Customer.class)); // Customer should not be saved
    }
}
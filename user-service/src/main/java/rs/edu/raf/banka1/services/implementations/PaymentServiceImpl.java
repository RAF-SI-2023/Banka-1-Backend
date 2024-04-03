package rs.edu.raf.banka1.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.PaymentDto;
import rs.edu.raf.banka1.mapper.PaymentMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.Payment;
import rs.edu.raf.banka1.model.TransactionStatus;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.repositories.PaymentRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRequest;
import rs.edu.raf.banka1.services.EmailService;
import rs.edu.raf.banka1.services.PaymentService;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final PaymentMapper paymentMapper;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Autowired
    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            BankAccountRepository bankAccountRepository,
            CustomerRepository customerRepository,
            PaymentMapper paymentMapper,
            EmailService emailService,
            JwtUtil jwtUtil) {
        this.paymentRepository = paymentRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.customerRepository = customerRepository;
        this.paymentMapper = paymentMapper;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Long createPayment(CreatePaymentRequest request) {
        Payment payment = paymentMapper.createPaymentRequestToPayment(request);
        if (payment == null) return -1L;
        return paymentRepository.save(payment).getId();
    }

    @Override
    public void processPayment(Long paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            return;
        }
        Payment payment = paymentOpt.get();
        BankAccount senderAccount = payment.getSenderBankAccount();
        Optional<BankAccount> recipientAccountOpt = bankAccountRepository.findBankAccountByAccountNumber(payment.getRecipientAccountNumber());
        double commission = Payment.calculateCommission(payment.getAmount());
        //TODO: da li availableBalance ili balance??
        if (
                recipientAccountOpt.isEmpty()
                || payment.getStatus() != TransactionStatus.PROCESSING
                || payment.getAmount() + commission > senderAccount.getAvailableBalance()
                || !recipientAccountOpt.get().getCurrency().getId().equals(senderAccount.getCurrency().getId())
        ) {
            payment.setStatus(TransactionStatus.DENIED);
            paymentRepository.save(payment);
            return;
        }
        BankAccount recipientAccount = recipientAccountOpt.get();
        recipientAccount.setAvailableBalance(recipientAccount.getAvailableBalance() + payment.getAmount());
        senderAccount.setAvailableBalance(senderAccount.getAvailableBalance() - payment.getAmount() - commission);
        payment.setStatus(TransactionStatus.COMPLETE);

        bankAccountRepository.save(recipientAccount);
        bankAccountRepository.save(senderAccount);
        paymentRepository.save(payment);
    }

    @Override
    public List<PaymentDto> getAllPaymentsForAccountNumber(String accountNumber) {
        Optional<BankAccount> bankAccountOpt = bankAccountRepository.findBankAccountByAccountNumber(accountNumber);
        return bankAccountOpt.map(bankAccount ->
                bankAccount.getPayments().stream()
                .map(paymentMapper::paymentToPaymentDto)
                .collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    @Override
    public PaymentDto getPaymentById(Long id) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        return paymentOpt.map(paymentMapper::paymentToPaymentDto).orElse(null);
    }

    @Override
    public boolean sendSingleUseCode(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findByUserId(customerId);
        if (customerOpt.isEmpty()) return false;
        Customer customer = customerOpt.get();
        String singleUseCode = jwtUtil.generateSingleUseCode(customer.getEmail());
        customer.setSingleUseCode(singleUseCode);
        customerRepository.save(customer);
        return emailService.sendEmail(customer.getEmail(), "RAF Banka - Single use token",
                "Your single use code:\n" + singleUseCode);
    }

    @Override
    public boolean validatePayment(CreatePaymentRequest request) {
        Optional<BankAccount> bankAccountOpt = bankAccountRepository.findBankAccountByAccountNumber(request.getSenderAccountNumber());
        if (bankAccountOpt.isEmpty()) {
            return false;
        }
        Customer customer = bankAccountOpt.get().getCustomer();
        boolean valid = customer.getSingleUseCode() != null
                && request.getSingleUseCode() != null
                && !jwtUtil.isTokenExpired(request.getSingleUseCode())
                && customer.getSingleUseCode().equals(request.getSingleUseCode());
        if (valid) {
            customer.setSingleUseCode(null);
            customerRepository.save(customer);
        }
        return valid;
    }


}

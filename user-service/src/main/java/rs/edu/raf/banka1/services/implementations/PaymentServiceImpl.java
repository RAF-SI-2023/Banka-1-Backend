package rs.edu.raf.banka1.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.PaymentDto;
import rs.edu.raf.banka1.mapper.PaymentMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Payment;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.PaymentRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRequest;
import rs.edu.raf.banka1.services.PaymentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, BankAccountRepository bankAccountRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Boolean createPayment(CreatePaymentRequest request) {
        Payment payment = paymentMapper.createPaymentRequestToPayment(request);
        if (payment == null) return false;
        paymentRepository.save(payment);
        return true;
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
}

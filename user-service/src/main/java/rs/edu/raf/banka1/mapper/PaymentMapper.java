package rs.edu.raf.banka1.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Payment;
import rs.edu.raf.banka1.model.TransactionStatus;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.PaymentRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRequest;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class PaymentMapper {
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public PaymentMapper(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public Payment CreatePaymentRequestToPayment(CreatePaymentRequest request) {
        Optional<BankAccount> senderAccountOpt = bankAccountRepository.findBankAccountByAccountNumber(request.getSenderAccountNumber());
        if (senderAccountOpt.isEmpty()) return null;
        BankAccount senderAccount = senderAccountOpt.get();
        Payment payment = new Payment();
        payment.setSenderBankAccount(senderAccount);
        payment.setRecipientName(request.getRecipientName());
        payment.setRecipientAccountNumber(request.getRecipientAccountNumber());
        payment.setAmount(request.getAmount());
        payment.setPaymentCode(request.getPaymentCode());
        payment.setModel(request.getModel());
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setStatus(TransactionStatus.PROCESSING);
        payment.setCommissionFee(1.0);
        payment.setDateOfPayment(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond());
        payment.setChannel("web");
        payment.setPaymentPurpose(request.getPaymentPurpose());

        return payment;
    }
}

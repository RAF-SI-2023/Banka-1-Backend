package rs.edu.raf.banka1.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.PaymentDto;
import rs.edu.raf.banka1.exceptions.BankAccountNotFoundException;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Payment;
import rs.edu.raf.banka1.model.TransactionStatus;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
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

    public PaymentDto paymentToPaymentDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(payment.getId());
        paymentDto.setSenderAccountOwnerName(
                payment.getSenderBankAccount().getCustomer().getFirstName()
                    + " "
                    + payment.getSenderBankAccount().getCustomer().getLastName()
        );
        paymentDto.setSenderAccountNumber(payment.getSenderBankAccount().getAccountNumber());
        paymentDto.setRecipientAccountOwnerName(payment.getRecipientName());
        paymentDto.setRecipientAccountNumber(payment.getRecipientAccountNumber());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setPaymentCode(payment.getPaymentCode());
        paymentDto.setModel(payment.getModel());
        paymentDto.setReferenceNumber(payment.getReferenceNumber());
        paymentDto.setStatus(payment.getStatus());
        paymentDto.setCommissionFee(payment.getCommissionFee());
        paymentDto.setDateOfPayment(payment.getDateOfPayment());
        paymentDto.setChannel(payment.getChannel());

        return paymentDto;
    }

    public Payment createPaymentRequestToPayment(CreatePaymentRequest request) {
        BankAccount senderAccount = bankAccountRepository.findBankAccountByAccountNumber(request.getSenderAccountNumber())
            .orElseThrow(BankAccountNotFoundException::new);
        Payment payment = new Payment();
        payment.setSenderBankAccount(senderAccount);
        payment.setRecipientName(request.getRecipientName());
        payment.setRecipientAccountNumber(request.getRecipientAccountNumber());
        payment.setAmount(request.getAmount());
        payment.setPaymentCode(request.getPaymentCode());
        payment.setModel(request.getModel());
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setStatus(TransactionStatus.PROCESSING);
        payment.setCommissionFee(Payment.calculateCommission(request.getAmount()));
        payment.setDateOfPayment(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond());
        payment.setChannel("web");
        payment.setPaymentPurpose(request.getPaymentPurpose());

        return payment;
    }
}

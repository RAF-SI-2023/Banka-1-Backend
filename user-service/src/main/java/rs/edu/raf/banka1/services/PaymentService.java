package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.PaymentDto;
import rs.edu.raf.banka1.requests.CreatePaymentRequest;

import java.util.List;

public interface PaymentService {
    Boolean createPayment(CreatePaymentRequest request);

    List<PaymentDto> getAllPaymentsForAccountNumber(String accountNumber);

    PaymentDto getPaymentById(Long id);
}

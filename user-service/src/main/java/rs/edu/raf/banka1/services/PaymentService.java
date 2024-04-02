package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.requests.CreatePaymentRequest;

public interface PaymentService {
    Boolean createPayment(CreatePaymentRequest request);
}

package rs.edu.raf.banka1.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.PaymentMapper;
import rs.edu.raf.banka1.model.Payment;
import rs.edu.raf.banka1.repositories.PaymentRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRequest;
import rs.edu.raf.banka1.services.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Boolean createPayment(CreatePaymentRequest request) {
        Payment payment = paymentMapper.CreatePaymentRequestToPayment(request);
        if (payment == null) return false;
        paymentRepository.save(payment);
        return true;
    }
}

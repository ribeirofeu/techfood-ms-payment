package com.fiap.techfood.payment.domain.interfaces.gateway;

import com.fiap.techfood.payment.domain.payment.Payment;

import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> findById(long id);

    Payment save(Payment payment);

    void updatePaymentStatus(Payment payment);
}

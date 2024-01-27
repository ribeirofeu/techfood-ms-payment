package com.fiap.techfood.payment.infrastructure.repository;

import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
import com.fiap.techfood.payment.domain.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    @Autowired
    private final SpringPaymentRepository repository;

    public PaymentRepositoryImpl(SpringPaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Payment> findById(long id) {
        Optional<PaymentEntity> entity = repository.findById(id);
        return entity.map(PaymentEntity::toPayment);
    }

    @Override
    @Transactional
    public Payment save(Payment payment) {
        PaymentEntity registeredPayment = repository.saveAndFlush(PaymentEntity.from(payment));
        return registeredPayment.toPayment();
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Payment payment) {
        repository.updatePaymentStatus(payment.getId(), payment.getStatus());
    }
}

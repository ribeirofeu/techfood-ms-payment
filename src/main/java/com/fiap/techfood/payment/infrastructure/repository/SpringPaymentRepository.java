package com.fiap.techfood.payment.infrastructure.repository;

import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringPaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Modifying
    @Query("UPDATE PaymentEntity p SET p.status = :status where p.id = :id")
    void updatePaymentStatus(@Param(value = "id") long id, @Param(value = "status") PaymentStatus status);

}

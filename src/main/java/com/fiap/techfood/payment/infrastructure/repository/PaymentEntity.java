package com.fiap.techfood.payment.infrastructure.repository;

import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.payment.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "`payment`")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

    @Id
    @Column(name = "`id`", nullable = false, unique = true)
    private long id;

    @Column(name = "`customer_id`")
    private Long customerId;

    @Column(name = "`total_value`", nullable = false)
    private BigDecimal totalValue;

    @Column(name = "`qr_code`")
    private String qrCode;

    @Column(name = "`created_date`", nullable = false)
    private OffsetDateTime createdDateTime;

    @Column(name = "`status`", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private PaymentStatus status;

    public static PaymentEntity from(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId())
                .totalValue(payment.getTotalValue())
                .customerId(payment.getCustomerId())
                .createdDateTime(payment.getCreatedDateTime())
                .status(payment.getStatus())
                .build();
    }

    public Payment toPayment() {
        return Payment.builder()
                .id(this.id)
                .totalValue(this.totalValue)
                .customerId(this.customerId)
                .createdDateTime(this.createdDateTime)
                .status(this.status)
                .build();
    }
}

package com.glimmer.shopping.shoppingmall.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT_TRANSACTION")
@Data
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String orderNo;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime createTime;
}

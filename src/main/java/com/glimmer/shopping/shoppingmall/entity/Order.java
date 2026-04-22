package com.glimmer.shopping.shoppingmall.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Glimmer
 */
@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;   // PENDING, PAID, SHIPPED, COMPLETED
    private LocalDateTime createTime;
}

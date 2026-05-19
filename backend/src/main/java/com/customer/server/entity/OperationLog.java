package com.customer.server.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_log")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, length = 64)
    private String operator;

    @Column(nullable = false, length = 200)
    private String operation;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private WorkOrderStatus statusAfter;

    private LocalDateTime operateTime;

    @PrePersist
    protected void onCreate() {
        operateTime = LocalDateTime.now();
    }
}

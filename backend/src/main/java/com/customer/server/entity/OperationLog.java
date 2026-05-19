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
    private Long workOrderId;

    @Column(nullable = false, length = 64)
    private String operator;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private RoleType operatorRole;

    @Column(nullable = false, length = 200)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private WorkOrderStatus statusBefore;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private WorkOrderStatus statusAfter;

    @Column(length = 500)
    private String reason;

    private LocalDateTime operateTime;
}

package com.customer.server.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "work_order")
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 32)
    private String orderNo;

    @Column(nullable = false, length = 64)
    private String customerName;

    @Column(nullable = false, length = 32)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ProblemType problemType;

    @Column(nullable = false, length = 1000)
    private String problemDesc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Priority priority;

    @Column(length = 64)
    private String handler;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WorkOrderStatus status;

    @Column(length = 2000)
    private String processResult;

    @Column(length = 500)
    private String returnReason;

    @Column(length = 500)
    private String closeReason;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime assignTime;

    private LocalDateTime acceptTime;

    private LocalDateTime completeTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}

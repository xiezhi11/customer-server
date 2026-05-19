package com.customer.server.repository;

import com.customer.server.entity.WorkOrder;
import com.customer.server.entity.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    boolean existsByOrderNo(String orderNo);

    long countByStatus(WorkOrderStatus status);

    List<WorkOrder> findByCustomerNameOrderByCreateTimeDesc(String customerName);

    List<WorkOrder> findByHandlerOrderByCreateTimeDesc(String handler);
}

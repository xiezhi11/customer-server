package com.customer.server.repository;

import com.customer.server.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    List<OperationLog> findByWorkOrderIdOrderByOperateTimeDesc(Long workOrderId);
}

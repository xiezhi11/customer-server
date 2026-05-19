package com.customer.server.service;

import com.customer.server.dto.*;
import com.customer.server.entity.*;
import com.customer.server.repository.OperationLogRepository;
import com.customer.server.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Transactional
    public WorkOrder createWorkOrder(CreateWorkOrderDTO dto, String operator) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo(generateOrderNo());
        workOrder.setCustomerName(dto.getCustomerName());
        workOrder.setContact(dto.getContact());
        workOrder.setProblemType(dto.getProblemType());
        workOrder.setProblemDesc(dto.getProblemDesc());
        workOrder.setPriority(dto.getPriority());
        workOrder.setStatus(WorkOrderStatus.PENDING_ASSIGN);

        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(workOrder.getId(), operator, "创建工单", WorkOrderStatus.PENDING_ASSIGN);
        return workOrder;
    }

    @Transactional
    public WorkOrder assignWorkOrder(Long id, AssignWorkOrderDTO dto, String operator) {
        WorkOrder workOrder = getWorkOrderById(id);
        if (workOrder.getStatus() != WorkOrderStatus.PENDING_ASSIGN) {
            throw new RuntimeException("只有待分派状态的工单才能分派");
        }
        workOrder.setHandler(dto.getHandler());
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        workOrder.setAssignTime(LocalDateTime.now());
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, "分派工单给【" + dto.getHandler() + "】", WorkOrderStatus.ASSIGNED);
        return workOrder;
    }

    @Transactional
    public WorkOrder acceptWorkOrder(Long id, String operator) {
        WorkOrder workOrder = getWorkOrderById(id);
        if (workOrder.getStatus() != WorkOrderStatus.ASSIGNED) {
            throw new RuntimeException("只有已分派状态的工单才能接单");
        }
        workOrder.setStatus(WorkOrderStatus.PROCESSING);
        workOrder.setAcceptTime(LocalDateTime.now());
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, "接单", WorkOrderStatus.PROCESSING);
        return workOrder;
    }

    @Transactional
    public WorkOrder submitResult(Long id, SubmitResultDTO dto, String operator) {
        WorkOrder workOrder = getWorkOrderById(id);
        if (workOrder.getStatus() != WorkOrderStatus.PROCESSING && workOrder.getStatus() != WorkOrderStatus.RETURNED) {
            throw new RuntimeException("只有处理中或已退回状态的工单才能提交处理结果");
        }
        workOrder.setProcessResult(dto.getProcessResult());
        workOrder.setStatus(WorkOrderStatus.PENDING_CONFIRM);
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, "提交处理结果", WorkOrderStatus.PENDING_CONFIRM);
        return workOrder;
    }

    @Transactional
    public WorkOrder confirmComplete(Long id, String operator) {
        WorkOrder workOrder = getWorkOrderById(id);
        if (workOrder.getStatus() != WorkOrderStatus.PENDING_CONFIRM) {
            throw new RuntimeException("只有待确认状态的工单才能确认完成");
        }
        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setCompleteTime(LocalDateTime.now());
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, "确认完成", WorkOrderStatus.COMPLETED);
        return workOrder;
    }

    @Transactional
    public WorkOrder returnWorkOrder(Long id, ReturnWorkOrderDTO dto, String operator) {
        WorkOrder workOrder = getWorkOrderById(id);
        if (workOrder.getStatus() != WorkOrderStatus.PENDING_CONFIRM) {
            throw new RuntimeException("只有待确认状态的工单才能退回");
        }
        workOrder.setReturnReason(dto.getReturnReason());
        workOrder.setStatus(WorkOrderStatus.RETURNED);
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, "退回工单，原因：" + dto.getReturnReason(), WorkOrderStatus.RETURNED);
        return workOrder;
    }

    @Transactional
    public WorkOrder closeWorkOrder(Long id, CloseWorkOrderDTO dto, String operator) {
        WorkOrder workOrder = getWorkOrderById(id);
        WorkOrderStatus status = workOrder.getStatus();
        if (status != WorkOrderStatus.PENDING_ASSIGN && status != WorkOrderStatus.ASSIGNED
                && status != WorkOrderStatus.PROCESSING && status != WorkOrderStatus.RETURNED) {
            throw new RuntimeException("只有待分派、已分派、处理中、已退回状态的工单才能关闭");
        }
        workOrder.setCloseReason(dto.getCloseReason());
        workOrder.setStatus(WorkOrderStatus.CLOSED);
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, "关闭工单，原因：" + dto.getCloseReason(), WorkOrderStatus.CLOSED);
        return workOrder;
    }

    public List<WorkOrder> queryWorkOrders(WorkOrderQueryDTO dto, RoleType role, String currentUser) {
        Specification<WorkOrder> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (role == RoleType.CUSTOMER) {
                predicates.add(cb.equal(root.get("customerName"), currentUser));
            } else if (role == RoleType.HANDLER) {
                predicates.add(cb.equal(root.get("handler"), currentUser));
            }

            if (dto.getOrderNo() != null && !dto.getOrderNo().isEmpty()) {
                predicates.add(cb.like(root.get("orderNo"), "%" + dto.getOrderNo() + "%"));
            }
            if (dto.getCustomerName() != null && !dto.getCustomerName().isEmpty()) {
                predicates.add(cb.like(root.get("customerName"), "%" + dto.getCustomerName() + "%"));
            }
            if (dto.getProblemType() != null) {
                predicates.add(cb.equal(root.get("problemType"), dto.getProblemType()));
            }
            if (dto.getPriority() != null) {
                predicates.add(cb.equal(root.get("priority"), dto.getPriority()));
            }
            if (dto.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), dto.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "priority", "createTime");
        return workOrderRepository.findAll(spec, sort);
    }

    public WorkOrder getWorkOrderById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
    }

    public List<OperationLog> getOperationLogs(Long orderId) {
        return operationLogRepository.findByOrderIdOrderByOperateTimeDesc(orderId);
    }

    public StatisticsVO getStatistics(RoleType role, String currentUser) {
        List<WorkOrder> allOrders;
        if (role == RoleType.CUSTOMER) {
            allOrders = workOrderRepository.findByCustomerNameOrderByPriorityDescCreateTimeDesc(currentUser);
        } else if (role == RoleType.HANDLER) {
            allOrders = workOrderRepository.findByHandlerOrderByPriorityDescCreateTimeDesc(currentUser);
        } else {
            allOrders = workOrderRepository.findAll();
        }

        long total = allOrders.size();
        long pendingAssign = allOrders.stream().filter(o -> o.getStatus() == WorkOrderStatus.PENDING_ASSIGN).count();
        long processing = allOrders.stream().filter(o -> o.getStatus() == WorkOrderStatus.PROCESSING).count();
        long pendingConfirm = allOrders.stream().filter(o -> o.getStatus() == WorkOrderStatus.PENDING_CONFIRM).count();
        long completed = allOrders.stream().filter(o -> o.getStatus() == WorkOrderStatus.COMPLETED).count();
        long closed = allOrders.stream().filter(o -> o.getStatus() == WorkOrderStatus.CLOSED).count();

        return new StatisticsVO(total, pendingAssign, processing, pendingConfirm, completed, closed);
    }

    private String generateOrderNo() {
        String prefix = "WO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = 1;
        String orderNo;
        do {
            orderNo = prefix + String.format("%04d", seq++);
        } while (workOrderRepository.existsByOrderNo(orderNo));
        return orderNo;
    }

    private void saveOperationLog(Long orderId, String operator, String operation, WorkOrderStatus statusAfter) {
        OperationLog log = new OperationLog();
        log.setOrderId(orderId);
        log.setOperator(operator);
        log.setOperation(operation);
        log.setStatusAfter(statusAfter);
        operationLogRepository.save(log);
    }
}

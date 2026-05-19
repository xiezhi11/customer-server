package com.customer.server.service;

import com.customer.server.dto.*;
import com.customer.server.entity.*;
import com.customer.server.repository.OperationLogRepository;
import com.customer.server.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Transactional
    public WorkOrder createWorkOrder(CreateWorkOrderDTO dto, RoleType role, String operator) {
        if (role != RoleType.CUSTOMER) {
            throw new RuntimeException("只有客户角色可以创建工单");
        }
        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo(generateOrderNo());
        workOrder.setCustomerName(operator);
        workOrder.setContact(dto.getContact());
        workOrder.setProblemType(dto.getProblemType());
        workOrder.setProblemDesc(dto.getProblemDesc());
        workOrder.setPriority(dto.getPriority());
        workOrder.setStatus(WorkOrderStatus.PENDING_ASSIGN);

        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(workOrder.getId(), operator, role, "创建工单",
                null, WorkOrderStatus.PENDING_ASSIGN, null);
        return workOrder;
    }

    @Transactional
    public WorkOrder assignWorkOrder(Long id, AssignWorkOrderDTO dto, RoleType role, String operator) {
        if (role != RoleType.CUSTOMER_SERVICE) {
            throw new RuntimeException("只有客服角色可以分派工单");
        }
        WorkOrder workOrder = getWorkOrderById(id);
        WorkOrderStatus statusBefore = workOrder.getStatus();
        if (statusBefore != WorkOrderStatus.PENDING_ASSIGN) {
            throw new RuntimeException("只有待分派状态的工单才能分派");
        }
        workOrder.setHandler(dto.getHandler());
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        workOrder.setAssignTime(LocalDateTime.now());
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, role, "分派工单给【" + dto.getHandler() + "】",
                statusBefore, WorkOrderStatus.ASSIGNED, null);
        return workOrder;
    }

    @Transactional
    public WorkOrder acceptWorkOrder(Long id, RoleType role, String operator) {
        if (role != RoleType.HANDLER) {
            throw new RuntimeException("只有处理人角色可以接单");
        }
        WorkOrder workOrder = getWorkOrderById(id);
        WorkOrderStatus statusBefore = workOrder.getStatus();
        if (statusBefore != WorkOrderStatus.ASSIGNED) {
            throw new RuntimeException("只有已分派状态的工单才能接单");
        }
        if (!normalizeUser(operator).equals(normalizeUser(workOrder.getHandler()))) {
            throw new RuntimeException("只能接分派给自己的工单");
        }
        workOrder.setStatus(WorkOrderStatus.PROCESSING);
        workOrder.setAcceptTime(LocalDateTime.now());
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, role, "接单", statusBefore, WorkOrderStatus.PROCESSING, null);
        return workOrder;
    }

    @Transactional
    public WorkOrder submitResult(Long id, SubmitResultDTO dto, RoleType role, String operator) {
        if (role != RoleType.HANDLER) {
            throw new RuntimeException("只有处理人角色可以提交处理结果");
        }
        WorkOrder workOrder = getWorkOrderById(id);
        WorkOrderStatus statusBefore = workOrder.getStatus();
        if (statusBefore != WorkOrderStatus.PROCESSING && statusBefore != WorkOrderStatus.RETURNED) {
            throw new RuntimeException("只有处理中或已退回状态的工单才能提交处理结果");
        }
        if (!normalizeUser(operator).equals(normalizeUser(workOrder.getHandler()))) {
            throw new RuntimeException("只能处理分派给自己的工单");
        }
        workOrder.setProcessResult(dto.getProcessResult());
        workOrder.setStatus(WorkOrderStatus.PENDING_CONFIRM);
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, role, "提交处理结果", statusBefore, WorkOrderStatus.PENDING_CONFIRM, null);
        return workOrder;
    }

    @Transactional
    public WorkOrder confirmComplete(Long id, RoleType role, String operator) {
        if (role != RoleType.CUSTOMER) {
            throw new RuntimeException("只有客户角色可以确认完成");
        }
        WorkOrder workOrder = getWorkOrderById(id);
        WorkOrderStatus statusBefore = workOrder.getStatus();
        if (statusBefore != WorkOrderStatus.PENDING_CONFIRM) {
            throw new RuntimeException("只有待确认状态的工单才能确认完成");
        }
        if (!normalizeUser(operator).equals(normalizeUser(workOrder.getCustomerName()))) {
            throw new RuntimeException("只能确认自己提交的工单");
        }
        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setCompleteTime(LocalDateTime.now());
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, role, "确认完成", statusBefore, WorkOrderStatus.COMPLETED, null);
        return workOrder;
    }

    @Transactional
    public WorkOrder returnWorkOrder(Long id, ReturnWorkOrderDTO dto, RoleType role, String operator) {
        if (role != RoleType.CUSTOMER) {
            throw new RuntimeException("只有客户角色可以退回工单");
        }
        WorkOrder workOrder = getWorkOrderById(id);
        WorkOrderStatus statusBefore = workOrder.getStatus();
        if (statusBefore != WorkOrderStatus.PENDING_CONFIRM) {
            throw new RuntimeException("只有待确认状态的工单才能退回");
        }
        if (!normalizeUser(operator).equals(normalizeUser(workOrder.getCustomerName()))) {
            throw new RuntimeException("只能退回自己提交的工单");
        }
        workOrder.setReturnReason(dto.getReturnReason());
        workOrder.setStatus(WorkOrderStatus.RETURNED);
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, role, "退回工单", statusBefore, WorkOrderStatus.RETURNED, dto.getReturnReason());
        return workOrder;
    }

    @Transactional
    public WorkOrder closeWorkOrder(Long id, CloseWorkOrderDTO dto, RoleType role, String operator) {
        if (role != RoleType.CUSTOMER_SERVICE) {
            throw new RuntimeException("只有客服角色可以关闭工单");
        }
        WorkOrder workOrder = getWorkOrderById(id);
        WorkOrderStatus statusBefore = workOrder.getStatus();
        if (statusBefore != WorkOrderStatus.PENDING_ASSIGN && statusBefore != WorkOrderStatus.ASSIGNED
                && statusBefore != WorkOrderStatus.PROCESSING && statusBefore != WorkOrderStatus.RETURNED) {
            throw new RuntimeException("只有待分派、已分派、处理中、已退回状态的工单才能关闭");
        }
        workOrder.setCloseReason(dto.getCloseReason());
        workOrder.setStatus(WorkOrderStatus.CLOSED);
        workOrder = workOrderRepository.save(workOrder);
        saveOperationLog(id, operator, role, "关闭工单", statusBefore, WorkOrderStatus.CLOSED, dto.getCloseReason());
        return workOrder;
    }

    public List<WorkOrder> queryWorkOrders(WorkOrderQueryDTO dto, RoleType role, String currentUser) {
        String normalizedUser = normalizeUser(currentUser);
        List<WorkOrder> allOrders = workOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));

        return allOrders.stream()
                .filter(order -> {
                    if (role == RoleType.CUSTOMER) {
                        if (!normalizedUser.equals(normalizeUser(order.getCustomerName()))) return false;
                    } else if (role == RoleType.HANDLER) {
                        if (order.getHandler() == null || !normalizedUser.equals(normalizeUser(order.getHandler()))) return false;
                    }
                    if (dto.getOrderNo() != null && !dto.getOrderNo().isEmpty()) {
                        if (!order.getOrderNo().contains(dto.getOrderNo())) return false;
                    }
                    if (dto.getCustomerName() != null && !dto.getCustomerName().isEmpty()) {
                        if (!normalizeUser(order.getCustomerName()).contains(normalizeUser(dto.getCustomerName()))) return false;
                    }
                    if (dto.getProblemType() != null && order.getProblemType() != dto.getProblemType()) {
                        return false;
                    }
                    if (dto.getPriority() != null && order.getPriority() != dto.getPriority()) {
                        return false;
                    }
                    if (dto.getStatus() != null && order.getStatus() != dto.getStatus()) {
                        return false;
                    }
                    return true;
                })
                .sorted(Comparator.comparing((WorkOrder o) -> o.getPriority().getLevel()).reversed()
                        .thenComparing(WorkOrder::getCreateTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public WorkOrder getWorkOrderById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在，ID: " + id));
    }

    public WorkOrder getWorkOrderById(Long id, RoleType role, String currentUser) {
        WorkOrder workOrder = getWorkOrderById(id);
        checkViewPermission(workOrder, role, currentUser);
        return workOrder;
    }

    public List<OperationLog> getOperationLogs(Long workOrderId, RoleType role, String currentUser) {
        WorkOrder workOrder = getWorkOrderById(workOrderId);
        checkViewPermission(workOrder, role, currentUser);
        return operationLogRepository.findByWorkOrderIdOrderByOperateTimeDesc(workOrderId);
    }

    public StatisticsVO getStatistics(RoleType role, String currentUser) {
        String normalizedUser = normalizeUser(currentUser);
        List<WorkOrder> allOrders = workOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));

        List<WorkOrder> filteredOrders = allOrders.stream()
                .filter(order -> {
                    if (role == RoleType.CUSTOMER) {
                        return normalizedUser.equals(normalizeUser(order.getCustomerName()));
                    } else if (role == RoleType.HANDLER) {
                        return order.getHandler() != null && normalizedUser.equals(normalizeUser(order.getHandler()));
                    }
                    return true;
                })
                .collect(Collectors.toList());

        long total = filteredOrders.size();
        long pendingAssign = filteredOrders.stream().filter(o -> o.getStatus() == WorkOrderStatus.PENDING_ASSIGN).count();
        long processing = filteredOrders.stream().filter(o -> o.getStatus() == WorkOrderStatus.PROCESSING).count();
        long pendingConfirm = filteredOrders.stream().filter(o -> o.getStatus() == WorkOrderStatus.PENDING_CONFIRM).count();
        long completed = filteredOrders.stream().filter(o -> o.getStatus() == WorkOrderStatus.COMPLETED).count();
        long closed = filteredOrders.stream().filter(o -> o.getStatus() == WorkOrderStatus.CLOSED).count();

        return new StatisticsVO(total, pendingAssign, processing, pendingConfirm, completed, closed);
    }

    private synchronized String generateOrderNo() {
        String prefix = "WO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = 1;
        while (true) {
            String orderNo = prefix + String.format("%04d", sequence);
            if (!workOrderRepository.existsByOrderNo(orderNo)) {
                return orderNo;
            }
            sequence++;
        }
    }

    private void saveOperationLog(Long workOrderId, String operator, RoleType operatorRole,
                                  String content, WorkOrderStatus statusBefore,
                                  WorkOrderStatus statusAfter, String reason) {
        OperationLog log = new OperationLog();
        log.setWorkOrderId(workOrderId);
        log.setOperator(operator);
        log.setOperatorRole(operatorRole);
        log.setContent(content);
        log.setStatusBefore(statusBefore);
        log.setStatusAfter(statusAfter);
        log.setReason(reason);
        log.setOperateTime(LocalDateTime.now());
        operationLogRepository.save(log);
    }

    private void checkViewPermission(WorkOrder workOrder, RoleType role, String currentUser) {
        String normalizedUser = normalizeUser(currentUser);
        if (role == RoleType.CUSTOMER && !normalizedUser.equals(normalizeUser(workOrder.getCustomerName()))) {
            throw new RuntimeException("只能查看自己提交的工单");
        }
        if (role == RoleType.HANDLER && !normalizedUser.equals(normalizeUser(workOrder.getHandler()))) {
            throw new RuntimeException("只能查看分派给自己的工单");
        }
    }

    private String normalizeUser(String value) {
        if (value == null) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}

package com.customer.server.controller;

import com.customer.server.dto.*;
import com.customer.server.entity.OperationLog;
import com.customer.server.entity.RoleType;
import com.customer.server.entity.WorkOrder;
import com.customer.server.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/workorders")
@CrossOrigin
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    private String getUsername(String header) {
        if (header == null) return "";
        try {
            return new String(header.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return header;
        }
    }

    @PostMapping
    public Result<WorkOrder> createWorkOrder(
            @Validated @RequestBody CreateWorkOrderDTO dto,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        String user = getUsername(currentUser);
        WorkOrder workOrder = workOrderService.createWorkOrder(dto, user);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/assign")
    public Result<WorkOrder> assignWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody AssignWorkOrderDTO dto,
            @RequestHeader("X-User") String currentUser) {
        String user = getUsername(currentUser);
        WorkOrder workOrder = workOrderService.assignWorkOrder(id, dto, user);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/accept")
    public Result<WorkOrder> acceptWorkOrder(
            @PathVariable Long id,
            @RequestHeader("X-User") String currentUser) {
        String user = getUsername(currentUser);
        WorkOrder workOrder = workOrderService.acceptWorkOrder(id, user);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/submit")
    public Result<WorkOrder> submitResult(
            @PathVariable Long id,
            @Validated @RequestBody SubmitResultDTO dto,
            @RequestHeader("X-User") String currentUser) {
        String user = getUsername(currentUser);
        WorkOrder workOrder = workOrderService.submitResult(id, dto, user);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/confirm")
    public Result<WorkOrder> confirmComplete(
            @PathVariable Long id,
            @RequestHeader("X-User") String currentUser) {
        String user = getUsername(currentUser);
        WorkOrder workOrder = workOrderService.confirmComplete(id, user);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/return")
    public Result<WorkOrder> returnWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody ReturnWorkOrderDTO dto,
            @RequestHeader("X-User") String currentUser) {
        String user = getUsername(currentUser);
        WorkOrder workOrder = workOrderService.returnWorkOrder(id, dto, user);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/close")
    public Result<WorkOrder> closeWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody CloseWorkOrderDTO dto,
            @RequestHeader("X-User") String currentUser) {
        String user = getUsername(currentUser);
        WorkOrder workOrder = workOrderService.closeWorkOrder(id, dto, user);
        return Result.success(workOrder);
    }

    @GetMapping
    public Result<List<WorkOrder>> queryWorkOrders(
            @ModelAttribute WorkOrderQueryDTO dto,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        String user = getUsername(currentUser);
        List<WorkOrder> workOrders = workOrderService.queryWorkOrders(dto, role, user);
        return Result.success(workOrders);
    }

    @GetMapping("/{id}")
    public Result<WorkOrder> getWorkOrderDetail(@PathVariable Long id) {
        WorkOrder workOrder = workOrderService.getWorkOrderById(id);
        return Result.success(workOrder);
    }

    @GetMapping("/{id}/logs")
    public Result<List<OperationLog>> getOperationLogs(@PathVariable Long id) {
        List<OperationLog> logs = workOrderService.getOperationLogs(id);
        return Result.success(logs);
    }

    @GetMapping("/statistics")
    public Result<StatisticsVO> getStatistics(
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        String user = getUsername(currentUser);
        StatisticsVO statistics = workOrderService.getStatistics(role, user);
        return Result.success(statistics);
    }
}

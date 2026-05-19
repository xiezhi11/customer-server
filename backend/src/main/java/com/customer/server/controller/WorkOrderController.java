package com.customer.server.controller;

import com.customer.server.dto.*;
import com.customer.server.entity.OperationLog;
import com.customer.server.entity.RoleType;
import com.customer.server.entity.WorkOrder;
import com.customer.server.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workorders")
@CrossOrigin
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    @PostMapping
    public Result<WorkOrder> createWorkOrder(
            @Validated @RequestBody CreateWorkOrderDTO dto,
            @RequestHeader("X-Role") RoleType role,
            @RequestHeader("X-User") String currentUser) {
        WorkOrder workOrder = workOrderService.createWorkOrder(dto, currentUser);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/assign")
    public Result<WorkOrder> assignWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody AssignWorkOrderDTO dto,
            @RequestHeader("X-User") String currentUser) {
        WorkOrder workOrder = workOrderService.assignWorkOrder(id, dto, currentUser);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/accept")
    public Result<WorkOrder> acceptWorkOrder(
            @PathVariable Long id,
            @RequestHeader("X-User") String currentUser) {
        WorkOrder workOrder = workOrderService.acceptWorkOrder(id, currentUser);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/submit")
    public Result<WorkOrder> submitResult(
            @PathVariable Long id,
            @Validated @RequestBody SubmitResultDTO dto,
            @RequestHeader("X-User") String currentUser) {
        WorkOrder workOrder = workOrderService.submitResult(id, dto, currentUser);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/confirm")
    public Result<WorkOrder> confirmComplete(
            @PathVariable Long id,
            @RequestHeader("X-User") String currentUser) {
        WorkOrder workOrder = workOrderService.confirmComplete(id, currentUser);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/return")
    public Result<WorkOrder> returnWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody ReturnWorkOrderDTO dto,
            @RequestHeader("X-User") String currentUser) {
        WorkOrder workOrder = workOrderService.returnWorkOrder(id, dto, currentUser);
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/close")
    public Result<WorkOrder> closeWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody CloseWorkOrderDTO dto,
            @RequestHeader("X-User") String currentUser) {
        WorkOrder workOrder = workOrderService.closeWorkOrder(id, dto, currentUser);
        return Result.success(workOrder);
    }

    @GetMapping
    public Result<List<WorkOrder>> queryWorkOrders(
            @ModelAttribute WorkOrderQueryDTO dto,
            @RequestHeader("X-Role") RoleType role,
            @RequestHeader("X-User") String currentUser) {
        List<WorkOrder> workOrders = workOrderService.queryWorkOrders(dto, role, currentUser);
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
            @RequestHeader("X-Role") RoleType role,
            @RequestHeader("X-User") String currentUser) {
        StatisticsVO statistics = workOrderService.getStatistics(role, currentUser);
        return Result.success(statistics);
    }
}

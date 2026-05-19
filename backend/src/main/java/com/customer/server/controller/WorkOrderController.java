package com.customer.server.controller;

import com.customer.server.dto.*;
import com.customer.server.entity.OperationLog;
import com.customer.server.entity.RoleType;
import com.customer.server.entity.WorkOrder;
import com.customer.server.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/workorders")
@CrossOrigin
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    private String decodeUser(String user) {
        if (user == null) {
            return "";
        }
        try {
            return URLDecoder.decode(user, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return user;
        }
    }

    @PostMapping
    public Result<WorkOrder> createWorkOrder(
            @Validated @RequestBody CreateWorkOrderDTO dto,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        WorkOrder workOrder = workOrderService.createWorkOrder(dto, role, decodeUser(currentUser));
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/assign")
    public Result<WorkOrder> assignWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody AssignWorkOrderDTO dto,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        WorkOrder workOrder = workOrderService.assignWorkOrder(id, dto, role, decodeUser(currentUser));
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/accept")
    public Result<WorkOrder> acceptWorkOrder(
            @PathVariable Long id,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        WorkOrder workOrder = workOrderService.acceptWorkOrder(id, role, decodeUser(currentUser));
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/submit")
    public Result<WorkOrder> submitResult(
            @PathVariable Long id,
            @Validated @RequestBody SubmitResultDTO dto,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        WorkOrder workOrder = workOrderService.submitResult(id, dto, role, decodeUser(currentUser));
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/confirm")
    public Result<WorkOrder> confirmComplete(
            @PathVariable Long id,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        WorkOrder workOrder = workOrderService.confirmComplete(id, role, decodeUser(currentUser));
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/return")
    public Result<WorkOrder> returnWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody ReturnWorkOrderDTO dto,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        WorkOrder workOrder = workOrderService.returnWorkOrder(id, dto, role, decodeUser(currentUser));
        return Result.success(workOrder);
    }

    @PutMapping("/{id}/close")
    public Result<WorkOrder> closeWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody CloseWorkOrderDTO dto,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        WorkOrder workOrder = workOrderService.closeWorkOrder(id, dto, role, decodeUser(currentUser));
        return Result.success(workOrder);
    }

    @GetMapping
    public Result<List<WorkOrder>> queryWorkOrders(
            @ModelAttribute WorkOrderQueryDTO dto,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        List<WorkOrder> workOrders = workOrderService.queryWorkOrders(dto, role, decodeUser(currentUser));
        return Result.success(workOrders);
    }

    @GetMapping("/{id}")
    public Result<WorkOrder> getWorkOrderDetail(
            @PathVariable Long id,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        WorkOrder workOrder = workOrderService.getWorkOrderById(id, role, decodeUser(currentUser));
        return Result.success(workOrder);
    }

    @GetMapping("/{id}/logs")
    public Result<List<OperationLog>> getOperationLogs(
            @PathVariable Long id,
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        List<OperationLog> logs = workOrderService.getOperationLogs(id, role, decodeUser(currentUser));
        return Result.success(logs);
    }

    @GetMapping("/statistics")
    public Result<StatisticsVO> getStatistics(
            @RequestHeader("X-Role") String roleStr,
            @RequestHeader("X-User") String currentUser) {
        RoleType role = RoleType.valueOf(roleStr);
        StatisticsVO statistics = workOrderService.getStatistics(role, decodeUser(currentUser));
        return Result.success(statistics);
    }
}

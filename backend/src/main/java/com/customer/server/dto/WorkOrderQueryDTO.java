package com.customer.server.dto;

import com.customer.server.entity.Priority;
import com.customer.server.entity.ProblemType;
import com.customer.server.entity.WorkOrderStatus;
import lombok.Data;

@Data
public class WorkOrderQueryDTO {
    private String orderNo;
    private String customerName;
    private ProblemType problemType;
    private Priority priority;
    private WorkOrderStatus status;
}

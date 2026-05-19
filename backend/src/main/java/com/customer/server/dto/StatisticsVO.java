package com.customer.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsVO {
    private long total;
    private long pendingAssign;
    private long processing;
    private long pendingConfirm;
    private long completed;
    private long closed;
}

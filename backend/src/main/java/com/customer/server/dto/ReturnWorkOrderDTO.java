package com.customer.server.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReturnWorkOrderDTO {
    @NotBlank(message = "退回原因不能为空")
    private String returnReason;
}

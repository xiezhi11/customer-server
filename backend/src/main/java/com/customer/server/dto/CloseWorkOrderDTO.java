package com.customer.server.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CloseWorkOrderDTO {
    @NotBlank(message = "关闭原因不能为空")
    private String closeReason;
}

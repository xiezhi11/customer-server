package com.customer.server.dto;

import com.customer.server.entity.Priority;
import com.customer.server.entity.ProblemType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateWorkOrderDTO {
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @NotBlank(message = "联系方式不能为空")
    private String contact;

    @NotNull(message = "问题类型不能为空")
    private ProblemType problemType;

    @NotBlank(message = "问题描述不能为空")
    private String problemDesc;

    @NotNull(message = "优先级不能为空")
    private Priority priority;
}

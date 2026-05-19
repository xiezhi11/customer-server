package com.customer.server.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SubmitResultDTO {
    @NotBlank(message = "处理结果不能为空")
    private String processResult;
}

package com.customer.server.entity;

public enum ProblemType {
    EQUIPMENT_FAULT("设备故障"),
    SYSTEM_EXCEPTION("系统异常"),
    OTHER("其他");

    private final String desc;

    ProblemType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

package com.customer.server.entity;

public enum RoleType {
    CUSTOMER("客户"),
    SERVICE("客服"),
    HANDLER("处理人");

    private final String desc;

    RoleType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

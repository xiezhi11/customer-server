package com.customer.server.entity;

public enum Priority {
    HIGH("高", 3),
    MEDIUM("中", 2),
    LOW("低", 1);

    private final String desc;
    private final int level;

    Priority(String desc, int level) {
        this.desc = desc;
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public int getLevel() {
        return level;
    }
}

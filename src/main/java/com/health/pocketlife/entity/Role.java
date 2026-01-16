package com.health.pocketlife.entity;

public enum Role {
    ROLE_USER("ROLE_USER"), ROLE_ADMIN("ROLE_ADMIN");

    private final String key;
    Role(String key) { this.key = key; }
    public String getKey() { return key; }
}

package com.queueflow.dto;

import jakarta.validation.constraints.NotNull;

public class AdminUserStatusRequest {

    @NotNull(message = "Account status is required")
    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
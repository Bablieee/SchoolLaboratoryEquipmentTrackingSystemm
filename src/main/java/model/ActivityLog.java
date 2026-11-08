package com.schoollaboratory.model;

public record ActivityLog(
        int logId,
        int userId,
        Integer equipmentId,
        String actionType,
        String description,
        String timestamp
) {
}

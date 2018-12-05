package com.taskpal.taskpal;

public enum DeadlinePreference {
    CLOSER, LONGER;

    public static DeadlinePreference toDeadlinePreference (String deadlinePreference) {
        try {
            return valueOf(deadlinePreference);
        } catch (Exception ex) {
            // For error cases
            return CLOSER;
        }
    }
}

package com.taskpal.taskpal;

public enum TimePreference {
    MORNING, DAY, NIGHT;

    public static TimePreference toTimePreference (String timePreference) {
        try {
            return valueOf(timePreference);
        } catch (Exception ex) {
            // For error cases
            return MORNING;
        }
    }
}

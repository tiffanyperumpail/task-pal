package com.taskpal.taskpal;

public enum AttentionPreference {
    NO_BREAKS, BREAKS;

    public static AttentionPreference toAttentionPreference (String attentionPreference) {
        try {
            return valueOf(attentionPreference);
        } catch (Exception ex) {
            // For error cases
            return NO_BREAKS;
        }
    }

}

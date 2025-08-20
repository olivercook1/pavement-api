package com.pavement.api.domain;

/**
 * Traffic categories TC2..TC5 mapped from the string codes
 * you currently send from the UI ("2","3","4","5").
 * Using an enum keeps the service logic type-safe.
 */
public enum TrafficCategory {
    TC2("2"),
    TC3("3"),
    TC4("4"),
    TC5("5");

    private final String code;

    TrafficCategory(String code) {
        this.code = code;
    }

    /** Returns the DMRB code ("2","3","4","5"). */
    public String code() {
        return code;
    }

    /** Parse from the UI string ("2","3","4","5") to the enum. */
    public static TrafficCategory fromString(String s) {
        for (TrafficCategory tc : values()) {
            if (tc.code.equals(s)) {
                return tc;
            }
        }
        throw new IllegalArgumentException("Unknown traffic category: " + s);
    }
}

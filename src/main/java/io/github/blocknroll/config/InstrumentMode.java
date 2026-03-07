package io.github.blocknroll.config;

public enum InstrumentMode {
    NONE("None"),
    INSTRUMENT("Instrument"),
    PITCH("Pitch");

    private final String displayName;

    InstrumentMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public InstrumentMode next() {
        InstrumentMode[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}


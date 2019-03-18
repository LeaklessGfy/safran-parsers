package dataimport.reader;

import java.util.Objects;

final class MeasureReader {
    private final String name;
    private String type;
    private String unit;

    MeasureReader(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    String getType() {
        return type;
    }
    void setType(String type) {
        this.type = Objects.requireNonNull(type);
    }

    void setUnit(String unit) {
        this.unit = Objects.requireNonNull(unit);
    }
    String getUnit() {
        return unit;
    }
}

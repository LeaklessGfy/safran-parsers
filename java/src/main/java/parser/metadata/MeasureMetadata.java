package parser.metadata;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class MeasureMetadata {
    public static class Builder {
        public String name;
        public String type;
        public String unit;

        public Builder(String name) {
            this.name = name;
        }

        public MeasureMetadata build() {
            return new MeasureMetadata(this);
        }
    }

    private final String name;
    private final String type;
    private final String unit;

    private MeasureMetadata(Builder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.type = builder.type;
        this.unit = builder.unit;
    }
}

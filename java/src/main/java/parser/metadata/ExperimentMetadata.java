package parser.metadata;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class ExperimentMetadata {
    public static class Builder {
        public String startTime;
        public String endTime;
        public final List<MeasureMetadata> measures = new ArrayList<>();
        public final List<SampleMetadata> samples = new ArrayList<>();
        public final List<AlarmMetadata> alarms = new ArrayList<>();

        public ExperimentMetadata build() {
            return new ExperimentMetadata(this);
        }
    }

    private String startTime;
    private String endTime;
    private final List<MeasureMetadata> measures;
    private final List<SampleMetadata> samples;
    private final List<AlarmMetadata> alarms;

    private ExperimentMetadata(Builder builder) {
        this.startTime = Objects.requireNonNull(builder.startTime);
        this.endTime = Objects.requireNonNull(builder.endTime);
        this.measures = Objects.requireNonNull(builder.measures);
        this.samples = Objects.requireNonNull(builder.samples);
        this.alarms = Objects.requireNonNull(builder.alarms);
    }
}

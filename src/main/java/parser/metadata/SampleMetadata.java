package parser.metadata;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class SampleMetadata {
    private final long value;
    private final LocalDateTime time;

    public SampleMetadata(long value, LocalDateTime time) {
        this.value = value;
        this.time = Objects.requireNonNull(time);
    }
}

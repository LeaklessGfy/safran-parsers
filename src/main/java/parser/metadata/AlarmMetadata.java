package parser.metadata;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class AlarmMetadata {
    private String reference;
    private String name;
    private String state;
    private String order;
    private LocalDateTime time;
    private int level;
    private String message;
    private long experiment;

    public AlarmMetadata(
            String reference,
            String name,
            String state,
            String order,
            LocalDateTime time,
            int level,
            String message,
            long experiment
    ) {
        this.reference = reference;
        this.name = name;
        this.state = state;
        this.order = order;
        this.time = Objects.requireNonNull(time);
        this.level = level;
        this.message = Objects.requireNonNull(message);
        this.experiment = experiment;
    }
}

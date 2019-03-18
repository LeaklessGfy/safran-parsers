package dataimport.reader;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
final class AlarmMetadata {
    private String reference;
    private String name;
    private String state;
    private String order;
    private LocalDateTime time;
    private int level;
    private String message;
    private long experiment;

    AlarmMetadata(
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
        this.time = time;
        this.level = level;
        this.message = message;
        this.experiment = experiment;
    }
}

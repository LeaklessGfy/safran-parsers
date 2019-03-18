package dataimport.reader;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class ExperimentMetadata {
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static final class Alarm {
        private String reference;
        private String name;
        private String state;
        private String order;
        private LocalDateTime time;
        private int level;
        private String message;
        private long experiment;

        Alarm(
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

    private String startTime;
    private String endTime;
    private int nbMeasures;
    private final List<Alarm> alarmList = new ArrayList<>();

    String getStartTime() {
        return startTime;
    }
    void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    String getEndTime() {
        return endTime;
    }
    void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    int getNbMeasures() {
        return nbMeasures;
    }
    void setNbMeasures(int nbMeasures) {
        this.nbMeasures = nbMeasures;
    }

    boolean addAlarm(Alarm alarm) {
        return alarmList.add(alarm);
    }
    List<Alarm> getAlarmList() {
        return Collections.unmodifiableList(alarmList);
    }
}

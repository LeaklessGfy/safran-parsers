package dataimport.reader;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
final class ExperimentMetadata {
    String startTime;
    String endTime;
    final List<MeasureMetadata> measures = new ArrayList<>();
    final List<AlarmMetadata> alarms = new ArrayList<>();
}

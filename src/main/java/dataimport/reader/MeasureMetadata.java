package dataimport.reader;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
final class MeasureMetadata {
    private final String name;
    String type;
    String unit;

    MeasureMetadata(String name) {
        this.name = name;
    }
}

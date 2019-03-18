package parser;

import parser.metadata.AlarmMetadata;
import parser.metadata.ExperimentMetadata;
import parser.metadata.MeasureMetadata;
import parser.metadata.SampleMetadata;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public final class ExperimentParser {
    private final static int FIRST_COLUMN_SAMPLE_VALUE = 2;

    private final long experimentId;
    private final BufferedReader experimentReader;
    private final BufferedReader alarmsReader;
    private final ExperimentMetadata.Builder builder = new ExperimentMetadata.Builder();

    public ExperimentParser(long experimentId, BufferedReader experimentReader, BufferedReader alarmsReader) {
        this.experimentId = experimentId;
        this.experimentReader = Objects.requireNonNull(experimentReader);
        this.alarmsReader = Objects.requireNonNull(alarmsReader);
    }

    public ExperimentMetadata parse() throws IOException {
        parseHeader();
        parseMeasures();
        parseSamples();
        if (alarmsReader != null) {
            parseAlarms();
        }
        return builder.build();
    }

    private void parseHeader() throws IOException {
        builder.startTime = parseTime();
        builder.endTime = parseTime();
    }

    private void parseMeasures() throws IOException {
        List<MeasureMetadata.Builder> measureBuilders = Arrays.stream(experimentReader.readLine().split(";"))
                .skip(FIRST_COLUMN_SAMPLE_VALUE)
                .filter(el -> !el.isEmpty())
                .map(MeasureMetadata.Builder::new)
                .collect(Collectors.toList());

        parseTypeAndUnit(measureBuilders);

        for (MeasureMetadata.Builder measureBuilder : measureBuilders) {
            builder.measures.add(measureBuilder.build());
        }
    }

    private void parseTypeAndUnit(List<MeasureMetadata.Builder> measureBuilders) throws IOException {
        experimentReader.readLine(); // ligne blanche
        List<String> listType = Arrays.stream(experimentReader.readLine().split(";"))
                .skip(FIRST_COLUMN_SAMPLE_VALUE)
                .collect(Collectors.toList());

        List<String> listUnit = Arrays.stream(experimentReader.readLine().split(";"))
                .skip(FIRST_COLUMN_SAMPLE_VALUE)
                .collect(Collectors.toList());

        for (int i = 0; i < listType.size(); i++) {
            MeasureMetadata.Builder m = measureBuilders.get(i);
            m.type = listType.get(i);
            String unit = listUnit.get(i);
            if (!unit.isEmpty()) {
                m.unit = unit;
            }
        }
    }

    private void parseSamples() throws IOException {
        String line;
        while ((line = experimentReader.readLine()) != null) {
            String[] samples = line.split(";");
            for (int i = 0; i < builder.measures.size(); i++) {
                if (
                        i + FIRST_COLUMN_SAMPLE_VALUE < samples.length
                        && samples[i + FIRST_COLUMN_SAMPLE_VALUE].length() > 0
                        && !samples[i + FIRST_COLUMN_SAMPLE_VALUE].equalsIgnoreCase("nan")
                ) {
                    builder.samples.add(new SampleMetadata(0, null)); // TODO: Find right data
                }
            }
        }
    }

    private void parseAlarms() throws IOException {
        String line;
        while ((line = alarmsReader.readLine()) != null) {
            String[] alarm = line.split(";");
            builder.alarms.add(new AlarmMetadata(
                    null,
                    null,
                    null,
                    null,
                    LocalDateTime.parse(builder.startTime.split("T")[0] + "T" + alarm[0].split(" ")[1]),
                    Integer.parseInt(alarm[1]),
                    alarm[2],
                    experimentId
            ));
        }
    }

    private String parseTime() throws IOException {
        return changeDecimalNotation(experimentReader.readLine().split(";")[1]);
    }

    private static String changeDecimalNotation(String string) {
        return string.replace(',', '.');
    }
}

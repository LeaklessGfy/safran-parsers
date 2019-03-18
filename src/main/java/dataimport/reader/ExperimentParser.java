package dataimport.reader;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public final class ExperimentParser {
    private final static int FIRST_COLUMN_SAMPLE_VALUE = 2;

    private final long experimentId;
    private final BufferedReader experimentReader;
    private final BufferedReader alarmsReader;
    private final ExperimentMetadata metadata = new ExperimentMetadata();

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
        return metadata;
    }

    private void parseHeader() throws IOException {
        metadata.startTime = parseTime();
        metadata.endTime = parseTime();
    }

    private void parseMeasures() throws IOException {
        List<MeasureMetadata> list = Arrays.stream(experimentReader.readLine().split(";"))
                .skip(FIRST_COLUMN_SAMPLE_VALUE)
                .filter(el -> !el.isEmpty())
                .map(MeasureMetadata::new)
                .collect(Collectors.toList());
        parseTypeAndUnit(list);
        metadata.measures.addAll(list);
    }

    private void parseTypeAndUnit(List<MeasureMetadata> measureReaderList) throws IOException {
        experimentReader.readLine(); // ligne blanche
        List<String> listType = Arrays.stream(experimentReader.readLine().split(";"))
                .skip(FIRST_COLUMN_SAMPLE_VALUE)
                .collect(Collectors.toList());

        List<String> listUnit = Arrays.stream(experimentReader.readLine().split(";"))
                .skip(FIRST_COLUMN_SAMPLE_VALUE)
                .collect(Collectors.toList());

        for (int i = 0; i < listType.size(); i++) {
            MeasureMetadata m = measureReaderList.get(i);
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
            List<String> samples = new ArrayList<>(Arrays.asList(line.split(";")));
            for (int i = 0; i < metadata.measures.size(); i++) {
                if (i + FIRST_COLUMN_SAMPLE_VALUE < samples.size()
                        && samples.get(i + FIRST_COLUMN_SAMPLE_VALUE).length() > 0
                        && !samples.get(i + FIRST_COLUMN_SAMPLE_VALUE).equalsIgnoreCase("nan")) {

                }
            }
        }
    }

    private void parseAlarms() throws IOException {
        String line;
        while ((line = alarmsReader.readLine()) != null) {
            String[] splitedLine = line.split(";");
            metadata.alarms.add(new AlarmMetadata(
                    null,
                    null,
                    null,
                    null,
                    LocalDateTime.parse(metadata.startTime.split("T")[0] + "T" + splitedLine[0].split(" ")[1]),
                    Integer.parseInt(splitedLine[1]),
                    splitedLine[2],
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

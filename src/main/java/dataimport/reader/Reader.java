package dataimport.reader;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public final class Reader {
    private final static int FIRST_COLUMN_SAMPLE_VALUE = 2;

    private BufferedReader measureBufferReader;
    private BufferedReader alarmBufferReader;
    private ExperimentMetadata metadata = new ExperimentMetadata();
    private final long id;


    private Reader(long id, BufferedReader measureBufferReader, BufferedReader alarmBufferReader) {
        if (id < 0) {
            throw new IllegalArgumentException("experimentId less than 0");
        }

        this.id = id;
        this.measureBufferReader = measureBufferReader;
        this.alarmBufferReader = alarmBufferReader;
    }

    public static Reader create(long id, BufferedReader measureMultipart, BufferedReader alarmsMultipart) {
        return new Reader(id, measureMultipart, alarmsMultipart);
    }

    public ExperimentMetadata createCSVFiles() throws IOException {
        metadata.setStartTime(getTime());
        metadata.setEndTime(getTime());

        List<MeasureReader> measureReaders = createMeasureFiles();
        int nbMeasure = createSampleFiles(measureReaders);
        metadata.setNbMeasures(nbMeasure);
        if (alarmBufferReader != null) {
            readAlarms();
        }

        return metadata;
    }

    private String getTime() throws IOException {
        return changeDecimalNotation(measureBufferReader.readLine().split(";")[1]);
    }

    private List<MeasureReader> createMeasureFiles() {
        try {
            List<MeasureReader> list = getMeasureReaders();
            addTypeAndUnity(list);
            return list;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int createSampleFiles(List<MeasureReader> measureReaders) throws IOException {
        String line;
        int indexFile = 1;
        while ((line = measureBufferReader.readLine()) != null) {
            List<String> samples = new ArrayList<>(Arrays.asList(line.split(";")));
            for (int i = 0; i < measureReaders.size(); i++) {
                if (i + FIRST_COLUMN_SAMPLE_VALUE < samples.size()
                        && samples.get(i + FIRST_COLUMN_SAMPLE_VALUE).length() > 0
                        && !samples.get(i + FIRST_COLUMN_SAMPLE_VALUE).equalsIgnoreCase("nan")) {

                }
            }
        }
        return indexFile;
    }

    private static String changeDecimalNotation(String string) {
        return string.replace(',', '.');
    }

    private List<MeasureReader> getMeasureReaders() throws IOException {
        return Arrays.stream(measureBufferReader.readLine().split(";"))
                .skip(FIRST_COLUMN_SAMPLE_VALUE)
                .filter(el -> !el.isEmpty())
                .map(MeasureReader::new)
                .collect(Collectors.toList());
    }

    private void addTypeAndUnity(List<MeasureReader> measureReaderList) throws IOException {
        measureBufferReader.readLine(); // ligne blanche
        List<String> listType = Arrays.stream(measureBufferReader.readLine().split(";")).skip(FIRST_COLUMN_SAMPLE_VALUE).collect(Collectors.toList());
        List<String> listUnity = Arrays.stream(measureBufferReader.readLine().split(";")).skip(FIRST_COLUMN_SAMPLE_VALUE).collect(Collectors.toList());
        for (int i = 0; i < listType.size(); i++) {
            MeasureReader m = measureReaderList.get(i);
            m.setType(listType.get(i));
            String unity = listUnity.get(i);
            if (!unity.isEmpty()) {
                m.setUnit(unity);
            }
        }
    }

    private void readAlarms() throws IOException {
        String line;
        while ((line = alarmBufferReader.readLine()) != null) {
            String[] splitedLine = line.split(";");
            metadata.addAlarm(new ExperimentMetadata.Alarm(
                    null,
                    null,
                    null,
                    null,
                    LocalDateTime.parse(metadata.getStartTime().split("T")[0] + "T" + splitedLine[0].split(" ")[1]),
                    Integer.parseInt(splitedLine[1]),
                    splitedLine[2],
                    id
            ));
        }
    }
}

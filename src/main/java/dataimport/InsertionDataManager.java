package dataimport;

import dataimport.reader.ExperimentParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class InsertionDataManager {
    public static void main(String[] args) throws IOException {
        try (
                FileReader fr1 = new FileReader("./bigfile.csv");
                FileReader fr2 = new FileReader("./event.csv");
                BufferedReader experimentFile = new BufferedReader(fr1);
                BufferedReader alarmsFile = new BufferedReader(fr2)
        ) {
            ObjectMapper mapper = new ObjectMapper();
            ExperimentParser parser = new ExperimentParser(0, experimentFile, alarmsFile);
            mapper.writeValue(new File("dump-big.json"), parser.parse());
        }
    }
}

package utility;

import com.opencsv.CSVReader;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.FileReader;
import java.util.List;

@UtilityClass
public class FileUtility {

    private final String PROJECT_DIR = System.getProperty("user.dir");
    private final String BASE_DIR = "\\src\\main\\resources\\data\\";

    @SneakyThrows
    public List<String[]> readFile(String file, boolean ignoreHeader) {
        var filereader = new FileReader(buildFilePath(file));
        var csvReader = new CSVReader(filereader);
        var readFile = csvReader.readAll();

        if (ignoreHeader) {
            readFile = readFile.subList(1, readFile.size());
        }

        return readFile;
    }

    private String buildFilePath(String fileName) {
        return PROJECT_DIR + "\\" + BASE_DIR + "\\" + fileName;
    }
}

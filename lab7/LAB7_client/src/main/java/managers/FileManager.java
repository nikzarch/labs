package managers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import collection.Ticket;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Класс для работы с файлами
 */
public class FileManager {
    /*
     * Парсит коллекцию из YAML файла
     */
    public static ArrayList<Ticket> loadCollectionFromFile(String path) throws IOException {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        om.findAndRegisterModules();
        if (Objects.isNull(path)) {
            ArrayList<Ticket> arr = new ArrayList<Ticket>();
            return arr;
        } else {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(path));
            ArrayList<Ticket> arr = om.readValue(inputStreamReader, new TypeReference<ArrayList<Ticket>>() {
            });
            return arr;
        }
    }

    /**
     * Сохраняет коллекцию в файл в формате YAML
     *
     * @param path
     * @param arr
     * @throws IOException
     */
    public static void saveData(String path, ArrayList arr) throws IOException {
        if (path.isEmpty()) {
            path = System.getenv("var");
        }
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        om.findAndRegisterModules();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(path));
        om.writeValue(outputStreamWriter, arr);
    }

    public static ArrayList<String> readLinesFromFile(BufferedReader reader) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
}

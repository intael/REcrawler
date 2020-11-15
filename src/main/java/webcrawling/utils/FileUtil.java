package webcrawling.utils;

import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
  public static final Path PROJECT_DIRECTORY =
      FileSystems.getDefault().getPath(".").normalize().toAbsolutePath();
  public static final Path RESOURCES_DIRECTORY =
      Paths.get(PROJECT_DIRECTORY.toString(), "src", "main", "resources");
  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

  public static List<String> readAllLinesFromFile(File file) {
    List<String> addresses = new ArrayList<>();
    String line;
    try (BufferedReader buffer = new BufferedReader(new FileReader(file))) {
      while ((line = buffer.readLine()) != null) {
        addresses.add(line);
        if (line.isEmpty()) {
          break;
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return addresses;
  }

  public Map<String, String> readPropertiesFile(String fileName) throws IOException {
    InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);
    if (stream == null) throw new FileNotFoundException("Could not find file " + fileName);
    Properties props = new Properties();
    props.load(stream);
    stream.close();
    return new HashMap<>(Maps.fromProperties(props));
  }
}

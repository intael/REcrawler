package webcrawling.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
  public static final Path projectDirectory =
      FileSystems.getDefault().getPath(".").normalize().toAbsolutePath();
  public static final Path resourcesDirectory =
      Paths.get(projectDirectory.toString(), "src", "main", "resources");

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
}

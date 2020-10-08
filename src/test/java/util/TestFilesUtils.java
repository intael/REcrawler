package util;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFilesUtils {

  public static final Path projectDirectory =
      FileSystems.getDefault().getPath(".").normalize().toAbsolutePath();
  public static final Path resourcesDirectory =
      Paths.get(projectDirectory.toString(), "src", "test", "resources");
  public static final Path htmlSamplesDirectory =
      Paths.get(resourcesDirectory.toString(), "html-samples");

  public static File getHtmlSampleAsFile(String fileName) {
    return Paths.get(htmlSamplesDirectory.toString(), fileName).toFile();
  }
}

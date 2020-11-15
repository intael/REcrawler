package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TestFilesUtils {

  public static final Path projectDirectory =
      FileSystems.getDefault().getPath(".").normalize().toAbsolutePath();
  public static final Path resourcesDirectory =
      Paths.get(projectDirectory.toString(), "src", "test", "resources");
  public static final Path htmlSamplesDirectory =
      Paths.get(resourcesDirectory.toString(), "html-samples");

  public static Document readAndParseHtmlFile(String domain, String fileName) throws IOException {
    File htmlFile = Paths.get(htmlSamplesDirectory.toString(), domain, fileName).toFile();
    return Jsoup.parse(htmlFile, null);
  }
}

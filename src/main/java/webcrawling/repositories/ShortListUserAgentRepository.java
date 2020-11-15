package webcrawling.repositories;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import webcrawling.utils.FileUtil;

public class ShortListUserAgentRepository implements UserAgentRepository {
  private static final String USER_AGENTS_DIRECTORY = "user-agents";
  private static final String CHROME_USER_AGENTS_FILE = "Chrome.txt";
  private final List<String> userAgents;

  public ShortListUserAgentRepository() {
    File userAgentsFilePath =
        Paths.get(
                FileUtil.RESOURCES_DIRECTORY.toString(),
                USER_AGENTS_DIRECTORY,
                CHROME_USER_AGENTS_FILE)
            .toFile();
    this.userAgents = FileUtil.readAllLinesFromFile(userAgentsFilePath);
  }

  public String getRandomUserAgent() {
    int listSize = userAgents.size();
    int randomIndex =
        ThreadLocalRandom.current()
            .nextInt(0, listSize); // // nextInt is normally exclusive of the top value,
    // value so add 1 to make it inclusive
    return userAgents.get(randomIndex);
  }
}

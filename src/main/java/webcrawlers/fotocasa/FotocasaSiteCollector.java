package webcrawlers.fotocasa;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawling.SiteCollector;

public class FotocasaSiteCollector implements SiteCollector {

  private String userAgent;
  private Map<String, String> cookies;
  private Proxy proxy;
  private static int TIMEOUT = 5_000;
  private static int MAX_RETRIES = 5;
  private final Logger logger = LoggerFactory.getLogger(FotocasaSiteCollector.class);

  public Document collect(String url) {
    Connection conn = Jsoup.connect(url);
    conn = addCookies(conn);
    int attempts = 0;
    while(attempts <= MAX_RETRIES){
      try{
        return conn.get();
      }
      catch (IOException requestError){
        attempts++;
        logger.error("Attempt: " + attempts + ".\nTrace: " + requestError.toString());
      }
    }
  }
  private Connection addCookies(Connection connection){
    for(String cookie: cookies.keySet()){
      connection = connection.cookie(cookie, cookies.get(cookie));
    }
    return connection;
  }
}

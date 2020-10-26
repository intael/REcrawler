package webcrawling.site_collectors;

public class SiteCollectorRanOutOfProxies extends Exception {
  public SiteCollectorRanOutOfProxies(String message) {
    super(message);
  }
}

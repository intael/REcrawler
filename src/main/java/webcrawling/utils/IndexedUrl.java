package webcrawling.utils;

import java.net.URL;
import org.jetbrains.annotations.NotNull;

public class IndexedUrl implements Comparable {

  private final URL url;
  private final int index;

  @Override
  public int compareTo(@NotNull Object o) {
    IndexedUrl other = (IndexedUrl) o;
    return Integer.compare(this.index, other.getIndex());
  }

  public IndexedUrl(URL url, int index) {
    this.url = url;
    this.index = index;
  }

  public URL getUrl() {
    return url;
  }

  public int getIndex() {
    return index;
  }
}

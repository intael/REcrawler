package webcrawling.repositories;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteRestProxyRepository implements ProxyRepository {
  private static final String NEWLINE_REGEX = "\\r?\\n";
  private final Map<Proxy, Proxy> proxies = new ConcurrentHashMap<>();
  private final Map<Proxy, Proxy> workingProxies = new ConcurrentHashMap<>();
  private final Map<Proxy, Proxy> faultyProxies = new ConcurrentHashMap<>();
  private final String restApiEndpoint;
  private final CloseableHttpClient httpClient;
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteRestProxyRepository.class);

  public RemoteRestProxyRepository(@NotNull String restApiEndpoint) {
    this.restApiEndpoint = restApiEndpoint;
    this.httpClient = HttpClients.createDefault();
  }

  @Override
  public void collectProxyList() {
    HttpGet request = new HttpGet(restApiEndpoint);
    try (CloseableHttpResponse response = httpClient.execute(request)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        String result = EntityUtils.toString(entity);
        proxies.putAll(
            Arrays.stream(result.split(NEWLINE_REGEX))
                .map(ProxyRepository::createProxyFromString)
                .distinct()
                .collect(Collectors.toMap((proxy) -> proxy, (proxy) -> proxy)));
      }
    } catch (IOException ioException) {
      LOGGER.error("Failed to collect new proxies.");
    }
  }

  private Proxy getRandomProxyFromMap(Map<Proxy, Proxy> map) {
    List<Proxy> keysAsArray = new ArrayList<>(map.keySet());
    Random random = new Random();
    return keysAsArray.get(random.nextInt(keysAsArray.size()));
  }

  @Override
  public Proxy getRandomUnusedProxy() {
    return getRandomProxyFromMap(proxies);
  }

  @Override
  public Proxy getRandomWorkingProxy() {
    return getRandomProxyFromMap(workingProxies);
  }

  @Override
  public synchronized void registerWorkingProxy(
      Proxy proxy) { // Not sure if the method needs to block since the Map is thread-safe
    faultyProxies.remove(proxy);
    proxies.remove(proxy);
    workingProxies.put(proxy, proxy);
  }

  @Override
  public synchronized void registerFaultyProxy(Proxy proxy) { // same as previous
    workingProxies.remove(proxy);
    proxies.remove(proxy);
    faultyProxies.put(proxy, proxy);
  }

  @Override
  public int getNumberOfUnusedProxies() {
    return this.proxies.size();
  }

  @Override
  public int getNumberOfWorkingProxies() {
    return this.workingProxies.size();
  }

  @Override
  public int getNumberOfFaultyProxies() {
    return this.faultyProxies.size();
  }

  @Override
  public void deregisterWorkingProxy(Proxy proxy) {
    this.workingProxies.remove(proxy);
    registerFaultyProxy(proxy);
  }

  @Override
  public Set<Proxy> getAllUnusedProxies() {
    return proxies.keySet(); // defensive copy
  }
}

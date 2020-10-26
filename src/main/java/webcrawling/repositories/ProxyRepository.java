package webcrawling.repositories;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Set;

public interface ProxyRepository {

  void collectProxyList() throws IOException;

  Proxy getRandomUnusedProxy();

  Proxy getRandomWorkingProxy();

  void registerWorkingProxy(Proxy proxy);

  void deregisterWorkingProxy(Proxy proxy);

  void registerFaultyProxy(Proxy proxy);

  int getNumberOfUnusedProxies();

  int getNumberOfWorkingProxies();

  int getNumberOfFaultyProxies();

  Set<Proxy> getAllUnusedProxies();

  static Proxy createProxyFromString(String proxyAddressString) {
    String[] addressComponents = proxyAddressString.split(":");
    InetSocketAddress proxyAddress =
        new InetSocketAddress(addressComponents[0], Integer.parseInt(addressComponents[1]));
    return new Proxy(Proxy.Type.HTTP, proxyAddress);
  }
}

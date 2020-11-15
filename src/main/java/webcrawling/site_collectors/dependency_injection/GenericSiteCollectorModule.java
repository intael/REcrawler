package webcrawling.site_collectors.dependency_injection;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import webcrawling.repositories.ProxyRepository;
import webcrawling.repositories.RemoteRestProxyRepository;
import webcrawling.repositories.ShortListUserAgentRepository;
import webcrawling.repositories.UserAgentRepository;
import webcrawling.repositories.dependency_injection.ProxyRepositoryQualifier;
import webcrawling.repositories.dependency_injection.UserAgentRepositoryQualifier;

public class GenericSiteCollectorModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Integer.class).annotatedWith(Names.named("timeoutInMiliseconds")).toInstance(30_000);
    bind(Integer.class).annotatedWith(Names.named("maxRetries")).toInstance(6);
  }

  @Provides
  @UserAgentRepositoryQualifier
  static UserAgentRepository provideUserAgentRepo() {
    return new ShortListUserAgentRepository();
  }

  @Provides
  @ProxyRepositoryQualifier
  static ProxyRepository provideProxyRepository() {
    return new RemoteRestProxyRepository(
        "https://api.proxyscrape.com/?request=getproxies&proxytype=http&timeout=750&country=all&ssl=all&anonymity=all");
  }
}

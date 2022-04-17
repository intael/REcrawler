package webcrawling.site_collectors.dependency_injection;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import java.util.List;

public class PlaywrightSiteCollectorModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Integer.class).annotatedWith(Names.named("maxRetries")).toInstance(6);
  }

  @Provides
  @LaunchOptionsQualifier
  static LaunchOptions provideLaunchOptions() {
    return new LaunchOptions().setTimeout(30_000).setIgnoreDefaultArgs(List.of("--mute-audio"));
  }
}

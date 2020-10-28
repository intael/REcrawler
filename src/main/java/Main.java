import java.util.Locale;
import webcrawlers.fotocasa.FotocasaSearchUrlBuilder;

public class Main {
  public static void main(String[] args) {
    FotocasaSearchUrlBuilder sre =
        new FotocasaSearchUrlBuilder.Builder(
                "barcelona", new Locale("en"))
            .build();
    System.out.println(sre.buildUrl());
  }
}

import java.util.Locale;
import webcrawlers.fotocasa.SearchRealEstateUrlBuilder;
import webcrawlers.fotocasa.specification.FotocasaContract;

public class Main {
  public static void main(String[] args) {
    SearchRealEstateUrlBuilder sre =
        new SearchRealEstateUrlBuilder.Builder(
                "barcelona", new Locale("en"))
            .build();
    System.out.println(sre.buildUrl());
  }
}

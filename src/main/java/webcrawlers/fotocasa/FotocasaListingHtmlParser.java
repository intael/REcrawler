package webcrawlers.fotocasa;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realestate.RealEstate;
import realestate.measures.Price;
import realestate.measures.Surface;
import webcrawlers.fotocasa.entities.FotocasaHome;
import webcrawlers.fotocasa.entities.FotocasaHouseId;
import webcrawling.HtmlParser;

public class FotocasaListingHtmlParser implements HtmlParser<RealEstate> {

  public static final Locale PAGE_LOCALE = new Locale("es", "ES");
  private static final String DETAIL_FEATURE_CONTENT_CSS_CLASS_NAME =
      "div.re-DetailFeaturesList-featureContent";
  private static final String PRICE_CONTAINER_CSS_CLASS_NAME = "span.re-DetailHeader-price";
  private static final String SURFACE_SPAN_CSS_CLASS_CRITERIA = "span:contains(sqm)";
  private static final String SURFACE_CONTAINER_CSS_CLASS_NAME = "li.re-DetailHeader-featuresItem";
  private static final String TITLE_CSS_CLASS_NAME = "h1.re-DetailHeader-propertyTitle";
  private static final String DESCRIPTION_CSS_CLASS_NAME = "p.fc-DetailDescription";
  private static final String AGENCY_NAME_CSS_CLASS_NAME =
      "span.re-ContactDetail-inmoContainer-clientName";
  private static final String REFERENCE_CONTAINER_CSS_CLASS_NAME =
      "div.re-ContactDetail-referenceContainer-wrapper";
  private static final String REFERENCE_LABEL_CSS_CLASS_NAME = "label.re-ContactDetail-label";
  private static final String REFERENCE_TEXT_CSS_CLASS_NAME =
      "span.re-ContactDetail-referenceContainer-reference";
  private final Logger logger = LoggerFactory.getLogger(FotocasaListingHtmlParser.class);

  private final NumberFormat localeNumberFormat = NumberFormat.getNumberInstance(PAGE_LOCALE);

  @Override
  public List<RealEstate> parse(@NotNull Document document) {
    Elements references = document.select(REFERENCE_CONTAINER_CSS_CLASS_NAME);
    try {
      Price price = getPrice(document).orElseThrow(RequiredFieldNotFoundException::new);
      Surface surface = getSurface(document).orElseThrow(RequiredFieldNotFoundException::new);
      String fotocasaReference =
          getReference(references, FotocasaHouseId.FOTOCASA_REFERENCE.getHtmlLabelName())
              .orElseThrow(RequiredFieldNotFoundException::new);
      return List.of(
          new FotocasaHome.Builder(price, surface, fotocasaReference)
              .withDescription(
                  getOptionalUniqueElement(document, DESCRIPTION_CSS_CLASS_NAME).orElse(""))
              .withTitle(getOptionalUniqueElement(document, TITLE_CSS_CLASS_NAME).orElse(""))
              .withAgencyName(
                  getOptionalUniqueElement(document, AGENCY_NAME_CSS_CLASS_NAME).orElse(""))
              .withAgencyReference(
                  getReference(references, FotocasaHouseId.AGENCY_REFERENCE.getHtmlLabelName())
                      .orElse(""))
              .build());
    } catch (RequiredFieldNotFoundException fieldNotFound) {
      logger.error("Failed to fetch required field.");
      return List.of();
    }
  }

  private Optional<String> getReference(Elements htmlContainer, String referenceType) {
    return htmlContainer.stream()
        .filter(
            (element ->
                element.select(REFERENCE_LABEL_CSS_CLASS_NAME).text().equals(referenceType)))
        .map((element -> element.select(REFERENCE_TEXT_CSS_CLASS_NAME).text()))
        .findFirst();
  }

  private Optional<Surface> getSurface(Document document) {
    Optional<Surface> surface = Optional.empty();
    Elements detailFeaturesValue = document.select(SURFACE_CONTAINER_CSS_CLASS_NAME);
    String surfaceString =
        detailFeaturesValue.select(SURFACE_SPAN_CSS_CLASS_CRITERIA).first().child(0).text();
    try {
      Number surfaceAmount = localeNumberFormat.parse(surfaceString);
      surface = Optional.of(new Surface(surfaceAmount.doubleValue()));
    } catch (ParseException pe) {
      logger.error(
          "Failed to parse surface from the string: "
              + surfaceString
              + ". Details: "
              + pe.toString());
    }
    return surface;
  }

  private Optional<Price> getPrice(Document document) {
    Optional<Price> price = Optional.empty();
    Element priceElement = document.selectFirst(PRICE_CONTAINER_CSS_CLASS_NAME);
    String priceString = priceElement.text();
    try {
      Number priceAmount = localeNumberFormat.parse(priceString);
      price =
          Optional.of(
              new Price(priceAmount.doubleValue(), localeNumberFormat.getCurrency().toString()));
    } catch (ParseException pe) {
      logger.error(
          "Failed to parse price from the string: " + priceString + ". Details: " + pe.toString());
    }
    return price;
  }

  private Optional<String> getOptionalUniqueElement(Document document, String cssClassName) {
    return Optional.of(document.selectFirst(cssClassName).text());
  }
}

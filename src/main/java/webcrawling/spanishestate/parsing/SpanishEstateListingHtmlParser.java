package webcrawling.spanishestate.parsing;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import webcrawlers.fotocasa.RequiredFieldNotFound;
import webcrawling.parsing.HtmlParser;
import webcrawling.spanishestate.entities.SpanishEstateHome;

public class SpanishEstateListingHtmlParser implements HtmlParser<RealEstate> {

  private static final String DETAILS_CONTAINER_CSS_CLASS_NAME = "div.detail-overview";
  private static final String TITLE_CONTAINER_CSS_CLASS_NAME = "div.information-details";
  private static final String TITLE_H1_CSS_CLASS_NAME = "h1[itemprop = name]";
  private static final String DESCRIPTION_P_CSS_CLASS_NAME = "div[itemprop = description]";
  private static final String NUMBER_DETAIL_NAME = "Number";
  private static final String REFERENCE_DETAIL_NAME = "Reference";
  private static final String PRICE_DETAIL_NAME = "Price";
  private static final String REGION_DETAIL_NAME = "Region";
  private static final String LOCATION_DETAIL_NAME = "Location";
  private static final String TYPE_DETAIL_NAME = "Type";
  private static final String BEDROOMS_DETAIL_NAME = "Bedrooms";
  private static final String BATHROOMS_DETAIL_NAME = "Bathrooms";
  private static final String M2_DETAIL_NAME = "M2";
  private static final String PLOT_DETAIL_NAME = "Plot";
  private final Logger logger = LoggerFactory.getLogger(SpanishEstateListingHtmlParser.class);
  private final NumberFormat localeNumberFormat =
      NumberFormat.getNumberInstance(new Locale("es", "ES"));

  @Override
  public List<RealEstate> parse(@NotNull Document document) {
    Element detailsBox = document.selectFirst(DETAILS_CONTAINER_CSS_CLASS_NAME);
    if (detailsBox == null) {
      LOGGER.error("No detail box found in the page, returning empty list.");
      return List.of();
    }
    Elements detailsTable =
        detailsBox.selectFirst(TABLE_HTML_TAG).selectFirst(TBODY_HTML_TAG).select(TR_HTML_TAG);
    Map<String, String> details = collectDetailsFromHtmlTable(detailsTable);
    Optional<SpanishEstateHome.Builder> spanishEstateHomeBuilder = Optional.empty();
    try {
      Price price =
          parsePrice(details.get(PRICE_DETAIL_NAME))
              .orElseThrow(
                  () -> new RequiredFieldNotFound("price", DETAILS_CONTAINER_CSS_CLASS_NAME));
      spanishEstateHomeBuilder =
          Optional.of(
              new SpanishEstateHome.Builder(
                  Integer.parseInt(details.get(NUMBER_DETAIL_NAME)),
                  details.get(REFERENCE_DETAIL_NAME),
                  price));
    } catch (RequiredFieldNotFound | NumberFormatException fieldNotFound) {
      logger.error(fieldNotFound.getMessage());
      return List.of();
    }
    Optional<Integer> bedrooms = tryParseIntFromString(details.get(BEDROOMS_DETAIL_NAME));
    Optional<Integer> bathrooms = tryParseIntFromString(details.get(BATHROOMS_DETAIL_NAME));
    SpanishEstateHome.Builder builder =
        spanishEstateHomeBuilder
            .get()
            .withSurface(parseSurface(details.get(M2_DETAIL_NAME)).orElse(null))
            .withRegion(details.get(REGION_DETAIL_NAME))
            .withLocation(details.get(LOCATION_DETAIL_NAME))
            .withType(details.get(TYPE_DETAIL_NAME))
            .withPlot(parseSurface(details.get(PLOT_DETAIL_NAME)).orElse(null))
            .withTitle(parseTitle(document))
            .withDescription(parseDescription(document));
    bedrooms.ifPresent(builder::withBedrooms);
    bathrooms.ifPresent(builder::withBedrooms);
    return List.of(builder.build());
  }

  private Map<String, String> collectDetailsFromHtmlTable(Elements detailsTable) {
    Map<String, String> details = new HashMap<>();
    detailsTable.forEach(
        (e) -> {
          Elements cells = e.select(TD_HTML_TAG);
          String detailName = "";
          String detailValue = detailName;
          for (Element cell : cells) {
            if (cell.select(SPAN_HTML_TAG).size() > 0) {
              detailName = cell.select(SPAN_HTML_TAG).text();
            } else {
              detailValue = cell.text();
            }
          }
          details.put(detailName, detailValue);
        });
    return details;
  }

  private Optional<Price> parsePrice(String priceString) {
    Optional<Price> price = Optional.empty();
    try {
      Number priceAmount = localeNumberFormat.parse(priceString);
      price =
          Optional.of(
              new Price(priceAmount.doubleValue(), localeNumberFormat.getCurrency().toString()));
    } catch (ParseException pe) {
      logger.warn(
          "Failed to parse price from the string: " + priceString + ". Details: " + pe.toString());
    }
    return price;
  }

  private Optional<Surface> parseSurface(String surfaceString) {
    Optional<Surface> surface = Optional.empty();
    try {
      surface =
          Optional.of(
              new Surface(
                  Integer.parseInt(surfaceString.substring(0, surfaceString.indexOf("m2") - 1))));
    } catch (NumberFormatException formatException) {
      logger.warn(
          "Failed to parse surface from the string: "
              + surfaceString
              + ". Details: "
              + formatException.toString());
    }
    return surface;
  }

  private Optional<Integer> tryParseIntFromString(String intString) {
    try {
      return Optional.of(Integer.parseInt(intString));
    } catch (NumberFormatException numberFormatException) {
      return Optional.empty();
    }
  }

  private String parseTitle(Document document) {
    return document
        .selectFirst(TITLE_CONTAINER_CSS_CLASS_NAME)
        .selectFirst(TITLE_H1_CSS_CLASS_NAME)
        .text();
  }

  // mejorar esto
  private String parseDescription(Document document) {
    Element descriptionDiv = document.selectFirst(DESCRIPTION_P_CSS_CLASS_NAME);
    if (descriptionDiv == null) return null;
    Elements paragraphs = descriptionDiv.select(P_HTML_TAG);
    StringBuilder text = new StringBuilder();
    paragraphs.forEach((element) -> text.append(element.text()).append("\n"));
    return text.toString();
  }
}

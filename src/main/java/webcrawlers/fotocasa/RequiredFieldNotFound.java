package webcrawlers.fotocasa;

public class RequiredFieldNotFound extends Exception {
  private final String fieldName;
  private final String htmlParsingLibraryQuery;

  public RequiredFieldNotFound(String fieldName, String cssClassName) {
    super(
        String.format(
            "Failed to fetch required field: %s using html parsing query %s.",
            fieldName, cssClassName));
    this.fieldName = fieldName;
    this.htmlParsingLibraryQuery = cssClassName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getHtmlParsingLibraryQuery() {
    return htmlParsingLibraryQuery;
  }
}

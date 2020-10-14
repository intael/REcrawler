package webcrawlers.fotocasa.entities;

import org.jetbrains.annotations.NotNull;

public enum FotocasaHouseId {
  FOTOCASA_REFERENCE("Reference fotocasa"),
  AGENCY_REFERENCE("Reference");

  private final String htmlLabelName;

  FotocasaHouseId(@NotNull String htmlLabelName) {
    this.htmlLabelName = htmlLabelName;
  }

  public String getHtmlLabelName() {
    return htmlLabelName;
  }
}

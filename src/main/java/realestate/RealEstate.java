package realestate;

import java.util.Objects;
import realestate.measures.Price;
import realestate.measures.Surface;

public abstract class RealEstate {

  protected String id;
  protected Price price;
  protected Surface surface;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RealEstate that = (RealEstate) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

package webcrawlers.fotocasa.specification;

public enum Rooms {
  ALL("All"),
  ONE_PLUS("1+"),
  TWO_PLUS("2+"),
  THREE_PLUS("3+"),
  FOUR_PLUS("4+");

  public String getRooms() {
    return rooms;
  }

  private final String rooms;

  private Rooms(String rooms) {
    this.rooms = rooms;
  }
}

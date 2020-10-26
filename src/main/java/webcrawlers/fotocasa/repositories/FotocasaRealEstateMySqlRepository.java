package webcrawlers.fotocasa.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realestate.RealEstate;
import webcrawlers.fotocasa.entities.FotocasaHome;
import webcrawling.RealEstateRepository;

public class FotocasaRealEstateMySqlRepository implements RealEstateRepository {
  public static final String DATABASE = "MYSQL_DATABASE";
  public static final String USER = "MYSQL_USER";
  public static final String PASSWORD = "MYSQL_ROOT_PASSWORD";
  private static final Logger LOGGER =
      LoggerFactory.getLogger(FotocasaRealEstateMySqlRepository.class);

  @Override
  public void save(RealEstate realEstate) {
    FotocasaHome home = (FotocasaHome) realEstate; // fix this...
    try {
      // TODO: Move these to properties file
      String myDriver = "com.mysql.cj.jdbc.Driver";
      String myUrl = String.format("jdbc:mysql://127.0.0.1:33069/%s", System.getenv(DATABASE));
      Class.forName(myDriver);
      Connection conn =
          DriverManager.getConnection(myUrl, System.getenv(USER), System.getenv(PASSWORD));

      // TODO: Move the query into a file
      String query =
          " insert into fotocasa_home (price, currency, surface, title, description, fotocasa_reference, home_category)"
              + " values (?, ?, ?, ?, ?, ?, ?)";

      // create the mysql insert preparedstatement
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setDouble(1, home.getPrice().getAmount());
      preparedStmt.setString(2, home.getPrice().getCurrency());
      preparedStmt.setFloat(3, (float) home.getSurface().getAmount());
      preparedStmt.setString(4, home.getTitle());
      preparedStmt.setString(5, home.getDescription());
      preparedStmt.setString(6, home.getFotocasaReference());
      preparedStmt.setString(7, "BUY");

      // execute the preparedstatement
      preparedStmt.execute();

      conn.close();
    } catch (Exception e) {
      LOGGER.error("Could not save into MySQL: " + e.getMessage());
    }
  }
}

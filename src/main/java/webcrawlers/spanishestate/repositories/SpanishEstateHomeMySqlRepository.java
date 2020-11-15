package webcrawlers.spanishestate.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realestate.RealEstate;
import webcrawlers.spanishestate.entities.SpanishEstateHome;
import webcrawling.RealEstateRepository;

public class SpanishEstateHomeMySqlRepository implements RealEstateRepository {
  public static final String HOST = "HOST";
  public static final String PORT = "PORT";
  public static final String DATABASE = "MYSQL_DATABASE";
  public static final String USER = "MYSQL_USER";
  public static final String PASSWORD = "MYSQL_ROOT_PASSWORD";
  private static final Logger LOGGER =
      LoggerFactory.getLogger(SpanishEstateHomeMySqlRepository.class);

  @Override
  public void save(RealEstate realEstate) {
    SpanishEstateHome home = (SpanishEstateHome) realEstate; // fix this...
    try {
      // TODO: Move these to properties file
      String myDriver = "com.mysql.cj.jdbc.Driver";
      String myUrl =
          String.format(
              "jdbc:mysql://%s:%s/%s",
              System.getenv(HOST), System.getenv(PORT), System.getenv(DATABASE));
      Class.forName(myDriver);
      Connection conn =
          DriverManager.getConnection(myUrl, System.getenv(USER), System.getenv(PASSWORD));

      // TODO: Move the query into a file
      String query =
          " insert into spanishestate_home (senumber, sereference, price, currency, region, location, type, bedrooms, bathrooms, surface, plot, title, description, created_at)"
              + " values (?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?)";

      // create the mysql insert preparedstatement
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setDouble(1, home.getSeNumber());
      preparedStmt.setString(2, home.getSeReference());
      preparedStmt.setDouble(3, home.getPrice().getAmount());
      preparedStmt.setString(4, home.getPrice().getCurrency());
      preparedStmt.setString(5, home.getRegion());
      preparedStmt.setString(6, home.getLocation());

      preparedStmt.setString(7, home.getType());
      preparedStmt.setInt(8, home.getBedrooms());
      preparedStmt.setInt(9, home.getBathrooms());
      if (home.getSurface() == null) {
        preparedStmt.setNull(10, Types.DOUBLE);
      } else {
        preparedStmt.setDouble(10, home.getSurface().getAmount());
      }
      if (home.getPlot() == null) {
        preparedStmt.setNull(11, Types.DOUBLE);
      } else {
        preparedStmt.setDouble(11, home.getPlot().getAmount());
      }
      preparedStmt.setString(12, home.getTitle());
      preparedStmt.setString(13, home.getDescription());
      preparedStmt.setTimestamp(14, new Timestamp(System.currentTimeMillis()));

      // execute the preparedstatement
      preparedStmt.execute();

      conn.close();
    } catch (Exception e) {
      LOGGER.error("Could not save into MySQL: " + e.getMessage());
    }
  }
}

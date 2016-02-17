package setup;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import models.DBProps;

public class MakeTables {

  public static void main(String[] args) throws
    IOException, ClassNotFoundException, SQLException {
    
    Properties props = DBProps.getProps();
    System.out.format("\n---- database = %s\n", DBProps.which);
    
    Helpers.createTables(props);
    Helpers.populateTables(props);
  }
}

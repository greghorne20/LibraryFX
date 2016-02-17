package setup;

import java.util.Properties;
import models.DBProps;

public class PopulateTables {
  public static void main(String[] args) throws ClassNotFoundException {
    Properties props = DBProps.getProps();
    System.out.format("\n---- database = %s\n", DBProps.which);
    
    Helpers.populateTables(props);
  }
}

package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class User extends Model {

  public static final String TABLE = "user";
   
  private int id = 0;
  private String name;
  private String email;

  public User() { }

  public User(String name, String email) {
    this.name = name;
    this.email = email;
  }
  
  @Override
  public int getId() { return id; }
  
  public String getName() { return name; }
  public String getEmail() { return email; }

  public void setName(String name) {this.name = name; }
  public void setEmail(String email) { this.email = email; }
  
  @Override
  void load(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    name = rs.getString("name");
    email = rs.getString("email");
  }
 
  @Override
  void insert() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
      "insert into %s (name,email) values (?,?)", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, name);
    st.setString(++i, email);
    st.executeUpdate();
    id = ORM.getMaxId(TABLE);
  }

  @Override
  void update() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
            "update %s set name=?,email=? where id=?", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, name);
    st.setString(++i, email);
    st.setInt(++i,id);
    st.executeUpdate();
  }
  
  @Override
  public String toString() {
    return String.format("(%s,%s,%s)", id, name, email);
  }
}

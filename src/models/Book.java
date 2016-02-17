package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class Book extends Model {

  public static final String TABLE = "book";
    
  private int id = 0;
  private String title;
  private String binding;
  private int quantity = 0;

  // must have an empty constructor defined
  public Book() { }

  public Book(String title, String binding, int quantity) {
    this.title = title;
    this.binding = binding;
    this.quantity = quantity;
  }

  @Override
  public int getId() { return id;}
  
  public String getTitle() { return title; }
  public String getBinding() { return binding; }
  public int getQuantity() { return quantity; }

  public void setTitle(String title) { this.title = title; }
  public void setBinding(String binding) { this.binding = binding; }
  public void setQuantity(int quantity) { this.quantity = quantity; }

  @Override
  void load(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    title = rs.getString("title");
    binding = rs.getString("binding");
    quantity = rs.getInt("quantity");
  }
  
  @Override
  void insert() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
      "insert into %s (title,binding,quantity) values (?,?,?)", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, title);
    st.setString(++i, binding);
    st.setInt(++i, quantity);
    st.executeUpdate();
    id = ORM.getMaxId(TABLE);
  }

  @Override
  void update() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
      "update %s set title=?,binding=?,quantity=? where id=?", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, title);
    st.setString(++i, binding);
    st.setInt(++i, quantity);
    st.setInt(++i, id);
    st.executeUpdate();
  }
  
  @Override
  public String toString() {
    return String.format("(%s,%s,%s,%s)", id, title, binding, quantity);
  }
}

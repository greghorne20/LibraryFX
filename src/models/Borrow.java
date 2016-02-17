package models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class Borrow extends Model {

  public static final String TABLE = "borrow";
    
  private int id = 0;
  private int user_id;
  private int book_id;
  private Date borrowed_at;

  // must have an empty constructor defined
  public Borrow() { }

  public Borrow(User user, Book book) {
    this.user_id = user.getId();
    this.book_id = book.getId();
  }

  public Borrow(User user, Book book, Date borrowed_at) {
    this.user_id = user.getId();
    this.book_id = book.getId();
    this.borrowed_at = borrowed_at;
  }

  public Borrow(Book book, User user) {
    this.user_id = user.getId();
    this.book_id = book.getId();
  }

  public Borrow(Book book, User user, Date borrowed_at) {
    this.user_id = user.getId();
    this.book_id = book.getId();
    this.borrowed_at = borrowed_at;
  }

  @Override
  public int getId() { return id;}
  
  public int getBookId() { return book_id; }
  public int getUserId() { return user_id; }
  public Date getBorrowedAt() { return borrowed_at; }

  public void setDate(Date borrowed_at) { this.borrowed_at = borrowed_at; }

  @Override
  void load(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    book_id = rs.getInt("book_id");
    user_id = rs.getInt("user_id");
    borrowed_at = rs.getDate("borrowed_at");
  }
  
  @Override
  void insert() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
      "insert into %s (book_id,user_id,borrowed_at) values (?,?,?)", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setInt(++i, book_id);
    st.setInt(++i, user_id);
    st.setDate(++i, borrowed_at);
    st.executeUpdate();
    id = ORM.getMaxId(TABLE);
  }

  @Override
  void update() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
      "update %s set borrowed_at=? where id=?", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setDate(++i, borrowed_at);
    st.setInt(++i, id);
    st.executeUpdate();
  }
  
  @Override
  public String toString() {
    return String.format("(%s,%s,%s,%s)", id, book_id, user_id, borrowed_at);
  }
}

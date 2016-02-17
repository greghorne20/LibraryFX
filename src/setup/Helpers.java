package setup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import java.util.Random;
import models.DBProps;
import models.ORM;
import models.Book;
import models.Borrow;
import models.User;

class Helpers {
  
  public static void createTables(Properties props) throws
    IOException, ClassNotFoundException, SQLException {
    
    final String BOOK = "book";
    final String USER = "user";
    final String BORROW = "borrow";

    String url = props.getProperty("url");
    String username = props.getProperty("username");
    String password = props.getProperty("password");
    String driver = props.getProperty("driver");
    if (driver != null) {
      Class.forName(driver); // load driver if necessary
    }
    Connection cx = DriverManager.getConnection(url, username, password);

    Statement stmt = cx.createStatement();

    System.out.format("\n---- drop tables\n");
    for (String table : new String[]{BORROW, BOOK, USER,}) {
      String sql = String.format("drop table if exists %s", table);
      System.out.println(sql);
      stmt.execute(sql);
    }

    System.out.format("\n---- create tables\n");
    for (String table : new String[]{USER, BOOK, BORROW,}) {
      String filename = String.format("tables/%s-%s.sql", table, DBProps.which);
      String sql = getResourceContent(filename).trim();

      System.out.println(sql);
      stmt.execute(sql);
    }
  }

  static void populateTables(Properties props) throws ClassNotFoundException {
    ORM.init(props);

    Book book1, book2, book3, book4, book5;

    Book[] books = new Book[]{
      book1 = new Book("Multimedia Systems", "cloth", 4),
      book2 = new Book("Java in a Nutshell", "paper", 4),
      book3 = new Book("Programming Perl", "paper", 4),
      book4 = new Book("Data Structures in Java", "cloth", 4),
      new Book("Java Foundation Classes", "paper", 4),
      new Book("Php in Action", "paper", 4),
      book5 = new Book("Machine Learning", "cloth", 4),
    };

    User john, kirsten, bill, mary, joan;

    User[] users = new User[]{
      john = new User("john", "arachnid@oracle.com"),
      kirsten = new User("kirsten", "buffalo@yahoo.com"),
      bill = new User("bill", "digger@gmail.com"),
      mary = new User("mary", "elephant@wcupa.edu"),
      joan = new User("joan", "kangaroo@upenn.edu"),};

    System.out.println("\n---- books");
    for (Book book : books) {
      try {
        ORM.store(book);
        System.out.println(book);
      }
      catch (Exception ex) {
        System.out.println(ex.getMessage());
      }
    }

    System.out.println("\n---- users");
    for (User user : users) {
      try {
        ORM.store(user);
        System.out.println(user);
      }
      catch (Exception ex) {
        System.out.println(ex.getMessage());
      }
    }

    class Pair {
      User user;
      Book book;

      Pair(User user, Book book) {
        this.book = book;
        this.user = user;
      }
    }

    Pair additions[] = new Pair[]{
      new Pair(john, book5),
      new Pair(bill, book3),
      new Pair(bill, book2),
      new Pair(bill, book5),
      new Pair(bill, book1),
      new Pair(mary, book3),
      new Pair(mary, book4),
      new Pair(joan, book4),
      new Pair(joan, book1),};

    System.out.println("\n---> borrows");
    for (Pair pair : additions) {
      try {
        java.sql.Date date = upToNdaysBeforeNow(60);
        Book book = pair.book;
        User user = pair.user;
        //ORM.addJoin(user, book, date);
        ORM.store(new Borrow(user, book, date));
        System.out.format("%s: %s => %s\n", date, user.getName(), book);
        pair.book.setQuantity(pair.book.getQuantity() - 1);
        ORM.store(pair.book);
        System.out.println("---> after: " + book + "\n");
      }
      catch (Exception ex) {
        System.out.println(ex.getMessage());
      }
    }
  }

  // support functions
  static String getResourceContent(String filename) throws IOException {
    StringBuilder content = new StringBuilder("");
    InputStream istr = CreateTables.class.getResourceAsStream(filename);
    InputStreamReader irdr = new InputStreamReader(istr);
    BufferedReader brdr = new BufferedReader(irdr);
    String line;
    while ((line = brdr.readLine()) != null) {
      content.append(line + "\n");
    }
    brdr.close();
    return content.toString();
  }

  private static Random rand = new Random();

  static java.sql.Date upToNdaysBeforeNow(int n) {
    long now = new java.util.Date().getTime(); // ms since 01/01/1970
    // before = now - [0-n days] * [hours/day] * [secs/hour] * [ms/sec]
    // you MUST cast rand.nextInt to long before computing with it,
    // otherwise the subtracted sub-expression may overflow integer range
    long before = now - (long) rand.nextInt(60) * 24 * 3600 * 1000;
    java.sql.Date date = new java.sql.Date(before);
    return date;
  }
}

package libraryfx;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Book;
import models.Borrow;
import models.DBProps;
import models.ORM;
import models.User;

/**
 * FXML Controller class
 *
 * @author Greg Horne
 */
public class LibraryController implements Initializable
{

    @FXML
    ListView<Book> booklist;

    @FXML
    ListView<User> userlist;

    Collection<Integer> userBookIds = new HashSet<>();

    @FXML
    TextArea display;

    static String bookInfo(Book book)
    {
        return String.format(
                "id: %s\n"
                + "title: %s\n"
                + "binding: %s\n"
                + "quantity: %s\n",
                book.getId(),
                book.getTitle(),
                book.getBinding(),
                book.getQuantity()
        );
    }

    static java.sql.Date currentDate()
    {
        long now = new java.util.Date().getTime();
        java.sql.Date date = new java.sql.Date(now);
        return date;
    }

    static String userInfo(User user)
    {
        return String.format(
                "id: %s\n"
                + "name: %s\n"
                + "email: %s\n",
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @FXML
    void userSelect(Event event)
    {
        User user = userlist.getSelectionModel().getSelectedItem();
        try
        {
            Collection<Borrow> borrows = ORM.findAll(Borrow.class, "where user_id=?", new Object[]
            {
                user.getId()
            });
            userBookIds.clear();
            for (Borrow borrow : borrows)
            {
                userBookIds.add(borrow.getBookId());
            }
            booklist.refresh();
            display.setText(userInfo(user));
        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err);
            System.exit(1);
        }

    }

    @FXML
    void bookSelect(Event event)
    {
        Book book = booklist.getSelectionModel().getSelectedItem();
        display.setText(bookInfo(book));
    }

    @FXML
    void bookUserStatus(Event event)
    {
        User user = userlist.getSelectionModel().getSelectedItem();
        Book book = booklist.getSelectionModel().getSelectedItem();
        try
        {
            if (user == null || book == null)
            {
                throw new ExpectedException("Must select book and user");
            }
            Borrow borrow = ORM.findOne(Borrow.class,
                    "where user_id=? and book_id=?", new Object[]
                    {
                        user.getId(), book.getId()
                    });
            if (borrow == null)
            {
                throw new ExpectedException("User does not have the book");
            }
            display.setText("borrowed on: " + borrow.getBorrowedAt());
        }
        catch (ExpectedException ex)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @FXML
    void bookReturn(Event event)
    {
        User user = userlist.getSelectionModel().getSelectedItem();
        Book book = booklist.getSelectionModel().getSelectedItem();
        try
        {
            if (user == null || book == null)
            {
                throw new ExpectedException("must select book and user");
            }
            Borrow borrow = ORM.findOne(Borrow.class,
                    "where user_id=? and book_id=?", new Object[]
                    {
                        user.getId(), book.getId()
                    });
            if (borrow == null)
            {
                throw new ExpectedException("user does not have the book");
            }
            ORM.remove(borrow);
            book.setQuantity(book.getQuantity() + 1);
            ORM.store(book);
            userBookIds.remove(book.getId());
            booklist.refresh();
            display.setText(bookInfo(book));
        }
        catch (ExpectedException ex)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @FXML
    void bookBorrow(Event event)
    {
        User user = userlist.getSelectionModel().getSelectedItem();
        Book book = booklist.getSelectionModel().getSelectedItem();
        try
        {
            if (user == null || book == null)
            {
                throw new ExpectedException("must select book and user");
            }
            Borrow borrow = ORM.findOne(Borrow.class,
                    "where user_id=? and book_id=?", new Object[]
                    {
                        user.getId(), book.getId()
                    });
            if (borrow != null)
            {
                throw new ExpectedException("user already has the book");
            }
            if (book.getQuantity() == 0)
            {
                throw new ExpectedException("no copies remaining");
            }
            borrow = new Borrow(book, user, currentDate());
            ORM.store(borrow);
            book.setQuantity(book.getQuantity() - 1);
            ORM.store(book);
            userBookIds.add(book.getId());
            booklist.refresh();
            display.setText("borrowed on: " + borrow.getBorrowedAt());
        }
        catch (ExpectedException ex)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @FXML
    void clear(Event event)
    {
        userlist.getSelectionModel().clearSelection();
        booklist.getSelectionModel().clearSelection();
        userBookIds.clear();
        booklist.refresh();
        display.setText("");
    }

    @FXML
    void addBookDialog(Event event)
    {
        try
        {
            // get fxmlLoader
            URL fxml = getClass().getResource("AddBookDialog.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxml);
            fxmlLoader.load();

            // get controller from fxml (must be set in FXML file)
            AddBookDialogController dialogController = fxmlLoader.getController();

            // get scene from loader
            Scene scene = new Scene(fxmlLoader.getRoot());

            Stage dialogStage = new Stage();
            dialogStage.setScene(scene);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // pass this controller to the dialog
            dialogController.mainController = this;

            // invoke the dialog
            dialogStage.show();

            // additional features
            
                  // query window closing
      dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
        public void handle(WindowEvent event) {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setContentText("Are you sure you want to exit this dialog?");
          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() != ButtonType.OK) {
            event.consume();
          }
        }
      });  

        }
        catch (IOException ex)
        {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @FXML
    void removeUser(Event event)
    {
        User user = userlist.getSelectionModel().getSelectedItem();
    try {
      if (user == null) {
        throw new ExpectedException("must select user");
      }
 
      // find all the book borrowed by the user
      Collection<Borrow> borrows = ORM.findAll(Borrow.class,
        "where user_id=?", new Object[]{user.getId()});
      if (! borrows.isEmpty() ) {
        throw new ExpectedException("user must return his/her books");
      }
 
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setContentText("Are you sure?");
      Optional<ButtonType> result = alert.showAndWait();
      if (result.get() != ButtonType.OK) {
        return;
      }
 
      // remove from user table
      ORM.remove(user);
 
      // remove from list
      userlist.getItems().remove(user);
 
      // if book is selected, display book, 
      // otherwise user must be selected, so clear display
      Book book = booklist.getSelectionModel().getSelectedItem();
      if (book != null) {
        display.setText(bookInfo(book));
      }
      else {
        display.setText("");
      }
    }
    catch (ExpectedException ex) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setContentText(ex.getMessage());
      alert.show();
    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }
    

    @FXML
  void modifyBookDialog(Event event) {
    Book book = booklist.getSelectionModel().getSelectedItem();
    try {
      if (book == null) {
        throw new ExpectedException("must choose a book");
      }
      // get fxmlLoader
      URL fxml = getClass().getResource("ModifyBookDialog.fxml");
      FXMLLoader fxmlLoader = new FXMLLoader(fxml);
      fxmlLoader.load();
 
      // get controller from fxml (must be set in FXML file)
      ModifyBookDialogController dialogController = fxmlLoader.getController();
 
      // get scene from loader
      Scene scene = new Scene(fxmlLoader.getRoot());
 
      Stage dialogStage = new Stage();
      dialogStage.setScene(scene);
      dialogStage.initModality(Modality.APPLICATION_MODAL);
 
      dialogController.mainController = this;
      dialogStage.show();
 
      //-------------------------------- key differences with addBookDialog
 
      // seed the fields with the values of the selected book
      dialogController.titleField.setText(book.getTitle());
      dialogController.quantityField.setText(String.valueOf(book.getQuantity()));
      dialogController.bindingSelection.getSelectionModel().select(
         book.getBinding()
      );
 
      // set the book to be modified in the dialogController
      dialogController.modifiedBook = book; 
    }
    catch (ExpectedException ex) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setContentText(ex.getMessage());
      alert.show();
    }
    catch (IOException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        BookCellCallback bookCellFactory = new BookCellCallback();
        booklist.setCellFactory(bookCellFactory);
        bookCellFactory.bookIds = userBookIds;

        try
        {
            ORM.init(DBProps.getProps());

            Collection<Book> books = ORM.findAll(Book.class);
            for (Book book : books)
            {
                booklist.getItems().add(book);
            }

            Collection<User> users = ORM.findAll(User.class);
            for (User user : users)
            {
                userlist.getItems().add(user);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

}

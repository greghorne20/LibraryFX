package libraryfx;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Book;
import models.ORM;

/**
 * FXML Controller class
 *
 * @author Greg Horne
 */
public class AddBookDialogController implements Initializable
{
    @FXML
    Node top;
    
    LibraryController mainController;
    
    @FXML
    TextField titleField;
    
    @FXML
    ComboBox<String> bindingSelection;
    
    @FXML
    TextField quantityField;
    
    @FXML
    void add(Event event)
    {
        String binding = bindingSelection.getSelectionModel().getSelectedItem();
        String title = titleField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        
        try
        {
            if(title.length() < 3)
            {
                throw new ExpectedException("title length must be at least 3");
            }
            if(!quantityStr.matches("\\d+"))
            {
                throw new ExpectedException("quantity must be a non-negative number");
            }
            int quantity = Integer.valueOf(quantityStr);
            
            Book bookWithTitle = ORM.findOne(Book.class, "where title=?", new Object[]{title});
            if (bookWithTitle != null)
            {
                throw new ExpectedException("existing book with same title");                        
            }
            // validation ok
            System.out.println("Validation ok");
            
            Book newBook = new Book(title, binding, quantity);
            ORM.store(newBook);
            
            //access the features of the Lbirary Controller
            ListView<Book> booklist = mainController.booklist;
            TextArea display = mainController.display;
            
            // reload booklist from DB
            booklist.getItems().clear();
            Collection<Book> books = ORM.findAll(Book.class);
            for(Book book : books)
            {
                booklist.getItems().add(book);
            }
            // select in list and scroll to added book
            booklist.getSelectionModel().select(newBook);
            booklist.scrollTo(newBook);
            // set text display to added book
            display.setText(LibraryController.bookInfo(newBook));
            
            top.getScene().getWindow().hide();
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
    void cancel(Event event)
    {
        top.getScene().getWindow().hide();
        
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        bindingSelection.getItems().add("paper");
        bindingSelection.getItems().add("cloth");
        bindingSelection.setValue("paper");
    }    
    
}

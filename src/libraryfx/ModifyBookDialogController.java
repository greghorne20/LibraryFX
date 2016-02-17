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
public class ModifyBookDialogController implements Initializable
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
    Book modifiedBook;
    
    @FXML
    void modify(Event event)
    {
        String binding = bindingSelection.getSelectionModel().getSelectedItem();
        String title = titleField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        try
        {
            if (binding == null)
            {
                throw new ExpectedException("must chose binding");
            }
            if (title.length() < 3)
            {
                throw new ExpectedException("title length must be at least 3");
            }
            if (!quantityStr.matches("\\d+"))
            {
                throw new ExpectedException("quantity must be a non-negative integer");
            }
            int quantity = Integer.valueOf(quantityStr);

            Book withTitle
                    = ORM.findOne(Book.class, "where title=?", new Object[]
                    {
                        title
            });
            if (withTitle != null && withTitle.getId() != modifiedBook.getId())
            {
                throw new ExpectedException("existing book with same title");
            }
            // validation OK

            // modify and reset record in table
            modifiedBook.setTitle(title);
            modifiedBook.setBinding(binding);
            modifiedBook.setQuantity(quantity);
            ORM.store(modifiedBook);

            // access the features of LibraryController
            ListView<Book> booklist = mainController.booklist;
            TextArea display = mainController.display;

            // make modification in place in booklist
            int index = booklist.getSelectionModel().getSelectedIndex();
            booklist.getItems().set(index, modifiedBook);

            // set text display to modified book
            display.setText(LibraryController.bookInfo(modifiedBook));

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

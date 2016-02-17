package libraryfx;

import java.util.Collection;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Book;

/**
 *
 * @author Greg Horne
 */
public class BookCellCallback implements Callback<ListView<Book>, ListCell<Book>>
{
    Collection<Integer> bookIds;
    
    @Override
    public ListCell<Book> call(ListView<Book> p)
    {
        ListCell<Book> cell = new ListCell<Book>()
        {
            @Override
            protected void updateItem(Book book, boolean empty)
            {
                super.updateItem(book, empty);
                if(empty)
                {
                    this.setText(null);
                    return;
                }
                this.setText(book.getTitle() + " | " + book.getQuantity());
                
                String css = "-fx-text-fill:#606; -fx-font-weight:bold;";
                if (bookIds.contains(book.getId()))
                {
                    this.setStyle(css);
                }
                else
                {
                    this.setStyle(null);
                }
            }
        };
        return cell;
    }
    
}

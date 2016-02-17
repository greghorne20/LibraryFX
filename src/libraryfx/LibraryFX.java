package libraryfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.DBProps;

/**
 *
 * @author Greg Horne
 */
public class LibraryFX extends Application
{
    
    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("Library.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        
        stage.setWidth(700);
        stage.setHeight(500);
        stage.setTitle("LibraryFX - " + DBProps.which);
        
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
}

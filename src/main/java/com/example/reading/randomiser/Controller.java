package com.example.reading.randomiser;

import com.example.reading.Paths;
import com.example.reading.challenge.AddChallengeController;
import com.example.reading.readingList.Book;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


//To send results between the 'add challenge' and 'randomise' window
public class Controller {

    private AddChallengeController addChallenge;

    public Controller(ObservableList<Book> books, AddChallengeController addChallenge) {

        try {
            this.addChallenge = addChallenge;

            //Load new window to show 'randomise' book options
            FXMLLoader load = new FXMLLoader(getClass().getResource("/com/example/reading/randomiser.fxml"));
            Scene scene = new Scene(load.load());
            scene.getStylesheets().add(getClass().getResource(Paths.randomiserCss).toExternalForm());
            Stage stage = new Stage();

            //Passing user's reading list to randomiser page
            RandomiserController randomController = load.getController();
            randomController.checkBooks(books, this); //Randomiser page displays 2 books
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //Passes user's selected book back to the 'add challenge' page
    public void getSelectedBook(Book selectedBook){
        addChallenge.setBookChoiceBox(selectedBook);
    }
}

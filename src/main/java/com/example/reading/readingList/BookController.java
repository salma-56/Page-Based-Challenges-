package com.example.reading.readingList;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

//Class to show reading list 
public class BookController implements Initializable {


    @FXML
    private Text myBooksTitle; //Title of page

    @FXML
    private ListView<Book> bookListView;
    private ObservableList<Book> bookObservableList;


    //Add and remove books
    @FXML
    private Button addBookButton;

    @FXML
    private Button removeBookButton;

    //Navigation
    @FXML
    private Button backButton;

    @FXML
    private Label removeBookLabel;

    //To access books in database:
    private BookDatabase db;
    private Connection conn;


    //For navigating between pages:
    private Parent root;
    private Stage stage;
    private Scene scene;



    //When page opens - find and show user's books
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        db = new BookDatabase();
        conn = db.connect("bookapp", "postgres", "oracle");

        //Retrieve books currently stored in database and display
        bookObservableList = db.readPrepData(conn);
        bookListView.setItems(bookObservableList);


        /*
        Johannes (2016) 'Custom ListCell in a JavaFX ListView', Turais, 23 May.
        Available at: https://www.turais.de/how-to-custom-listview-cell-in-javafx/ (Accessed: 11 April 2026).
         */
        //Used to modify default list appearance
        bookListView.setCellFactory(bookListView1 -> new BookListViewCell());

    }


    //Transitions to page for adding new books to reading list
    public void addBookButton (ActionEvent event) {
        System.out.println("A new add button");

        try {
             root = FXMLLoader.load(getClass().getResource("/com/example/reading/addBook.fxml"));
             stage = (Stage)((Node)event.getSource()).getScene().getWindow();
             scene = new Scene(root);
             scene.getStylesheets().add((getClass().getResource("/com/example/reading/stylesheets/addBook.css").toExternalForm()));
             stage.setScene(scene);
             stage.setResizable(false);
             stage.show();

        } catch (Exception e) {
            e.printStackTrace();        }




    }

    //User chooses book first and then removes
    //Function deletes book from database
    //Immediately updates view
    public void removeBookButton(ActionEvent event) {


        //Must select a book before clicking button
        //Do nothing if user has no books in reading list
        if(bookListView.getItems().isEmpty() || bookListView.getSelectionModel().isEmpty()) {
            System.out.println("AN EMPTY LIST");
            removeBookLabel.setText("Choose a book first!");
            removeBookLabel.setTextFill(Color.RED);
            removeBookLabel.setVisible(true);
        }

        else{

        removeBookLabel.setVisible(false);
        ObservableList<Book> selectionModel = bookListView.getSelectionModel().getSelectedItems();

        Book selectedBook = selectionModel.getFirst();
        db.deletePrep(conn, selectedBook.getBookId());

        bookListView.getItems().remove(selectedBook);
        }



    }


    //User presses back
    //Goes back to home page
    public void backToHomePage(ActionEvent event) {

        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/reading/homePage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/reading/stylesheets/homePage.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


}

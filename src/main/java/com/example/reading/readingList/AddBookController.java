package com.example.reading.readingList;

import com.example.reading.Paths;
import com.example.reading.api.ApiController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

//Adds book after user manually states book and author information
public class AddBookController implements Initializable {


    //To retrieve user's information
    @FXML
    private TextField bookTitleField;

    @FXML
    private TextField authorField;

    @FXML
    private Button addBookButton;

    //For navigation

    @FXML
    private Button backButton;

    //To change to an automatic search for book
    @FXML
    private ToggleButton toggle;

    //To print confirmation and error messages:
    @FXML
    private Label confirmLabel;

    @FXML
    private Label bookTitleAsterisk, authorNameAsterisk;

    @FXML
    private Text addBookManuallyTitle;



    //User clicks add book
    //Adds their book information to database, if correct details added
    //Prints confirmation message - success or fail
    public void addBook(ActionEvent event) {
        BookDatabase db = new BookDatabase();
        Connection conn = db.connect("bookapp", "postgres","oracle");

        //Get book title and author details
        String bookName = bookTitleField.getText();
        String authorName = authorField.getText();


        //Fields not blank - check book exists

        String check = db.checkBook(conn, bookName, authorName);



        //Book not in database, add
        if(check == null) {
            if (!bookName.isBlank() && !authorName.isBlank()) {
                db.prepInsert(conn, bookName, authorName);
                successfulLabel();
            }

            //A field was blank - show failure message
            else {
                failLabels(bookName, authorName);
            }
        }

        //Book exists in database
        else{
            confirmLabel.setText("Book already exists");
            confirmLabel.setTextFill(Color.RED);
            confirmLabel.setVisible(true);
        }

    }

    //Go back to the reading List
    public void backButton(ActionEvent event)  {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/reading/Controller.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(Paths.readingListCss).toExternalForm());
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }


    }


    //Show missing fields labels if book was not added
    public void failLabels(String bookTitle, String authorName) {

        //Hide error labels at start to avoid false messages
        bookTitleAsterisk.setVisible(false);
        authorNameAsterisk.setVisible(false);

        if(bookTitle.isBlank()) {
            bookTitleAsterisk.setText("*");
            bookTitleAsterisk.setTextFill(Color.RED);
            bookTitleAsterisk.setVisible(true);

        }

        if(authorName.isBlank()) {
            authorNameAsterisk.setText("*");
            authorNameAsterisk.setTextFill(Color.RED);
            authorNameAsterisk.setVisible(true);
        }


        confirmLabel.setText("Unsuccessful, check details");
        confirmLabel.setTextFill(Color.RED);
        confirmLabel.setVisible(true);
    }

    //Show this label if a book was added successfully:
    public void successfulLabel() {

        //Clear any failure messages to ensure no false messages printed
        if(bookTitleAsterisk.isVisible() || authorNameAsterisk.isVisible()) {
            bookTitleAsterisk.setVisible(false);
            authorNameAsterisk.setVisible(false);
        }


        confirmLabel.setVisible(true);
        confirmLabel.setText("Added Book!");
        confirmLabel.setTextFill(Color.GREEN);
    }

    //Change forms to do an API search instead:
    public void toggleApi(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        new ApiController(stage); //This class will load the fxml files + show view
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Make sure all confirmation labels cannot be seen - as user did not add a book yet
        bookTitleAsterisk.setVisible(false);
        confirmLabel.setVisible(false);
        authorNameAsterisk.setVisible(false);
    }



}

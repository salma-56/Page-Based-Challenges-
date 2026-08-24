package com.example.reading.api;

import com.example.reading.Paths;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

//User enters book information to begin searching
//Will retrieve book cover, book title, author name and pages (if info available)

//User adds chosen book to their reading list
public class AddBookAPIController implements Initializable {

    @FXML
    private Text bookSearchTitle;


    //User inputs book information
    @FXML
    private TextField bookName;

    @FXML
    private TextField authorName;

    //User searches for related books
    @FXML
    private Button searchBookButton;


    //User chooses book to add to their library
    @FXML
    private Button addBookButton;

    //Store books found by search
    @FXML
    private ComboBox<BookData> bookComboBox;


    //To confirm if book successfully added or not
    @FXML
    private Label confirmationLabel;


    //For navigation
    @FXML
    private ToggleButton manualToggle;

    //To show loading searches
    @FXML
    private ProgressIndicator progressIndicator;


    //To adapt GUI when API result received

    /*
    Created Consumer<Runnable> fields using:

    PragmaticCoding (2022) 'How to Build a JavaFX Application That Does Something', PragmaticCoding, February 6.
    Available at: https://www.pragmaticcoding.ca/javafx/weather (Accessed:11 April 2026).
     */
    private Consumer<Runnable> bookFetcher;
    private Consumer<Runnable> editionsFetcher;

    //Pass user's information to classes responsible for calling API
    private String query;
    private String editionQuery;
    private BookData selectedBook;


    //User adds details and searches

    public void searchBook(ActionEvent event) {

        //Remove old message when starting a new search
        confirmationLabel.setVisible(false);

        //Remove old results when searching
        bookComboBox.getItems().clear();

        addBookButton.setVisible(false);

        //Remove spaces from user inputs to make a valid url
        String bookNameQuery = bookName.getText().trim();
        bookNameQuery = bookNameQuery.replace(' ', '+');

        String authorNameQuery = authorName.getText().trim();
        authorNameQuery = authorNameQuery.replace(' ', '+');

        //Checking type of search
        boolean emptyBookField = bookName.getText().isBlank();
        boolean emptyAuthorField = authorName.getText().isBlank();


        //Only allow user to make search if there are no empty inputs
        if (emptyBookField && emptyAuthorField) {
            System.out.println("No query");
            confirmationLabel.setVisible(true);
            confirmationLabel.setText("Include a book and/or author");
            confirmationLabel.setTextFill(Color.RED);
            return;
        }


        //If here, a valid input. Build query to start API call.

        //User types book name - begin a query
        else if (!emptyBookField && emptyAuthorField) {
            query = "https://openlibrary.org/search.json?q=title:" + bookNameQuery + "+language:eng&fields=key,title,author_name,editions&limit=30";

        }

        //Author + book search for enhanced results
        else if (!emptyBookField) {

            query = "https://openlibrary.org/search.json?q=title:" + bookNameQuery +
                    "+AND+author:" + authorNameQuery + "+language:eng&fields=key,title,author_name,editions&limit=30";

        }

        //Retrieve books for named author
        else {
            query = "https://openlibrary.org/search.json?q=author:" + authorNameQuery + "+language:eng&fields=key,title,author_name,editions&limit=30";

        }


        //Do not allow users to click any buttons until thread complete

        adjustGui(true);

        //Once API finished responding + gui is updated, enable add button
        bookFetcher.accept(() ->
        {
            adjustGui(false);
            addBookButton.setVisible(true);
        });
         /* Used site to learn the .accept method for bookFetcher code above:

         PragmaticCoding (2022) 'How to Build a JavaFX Application That Does Something', PragmaticCoding, February 6.
         Available at: https://www.pragmaticcoding.ca/javafx/weather (Accessed:11 April 2026).
         */

    }


    //User chooses a book
    //Do an API call to receive edition-level information of a book - e.g cover image and page number
    public void addBook(ActionEvent event) {
        System.out.println("ADD");

        //Get the user's chosen book
        selectedBook = bookComboBox.getSelectionModel().getSelectedItem();


        //Do not do anything if user has not selected a book
        if (selectedBook == null) {
            confirmationLabel.setText("Choose a book first!");
            System.out.println("Select a book!");
        }

        //User has chosen a book, retrieve its edition url
        //Begin calling API to receive edition information
        else {

            String editionPath = selectedBook.getEditionPath();
            editionQuery = "https://openlibrary.org" + editionPath + ".json";

            //Do not allow user to do multiple searches until api responds
            adjustGui(true);

            //When complete, re-enable navigation + stop loading icon
            editionsFetcher.accept(() ->
            {
                adjustGui(false);

            });

        /* Used site to learn the .accept method for the editionsFetchers:
         PragmaticCoding (2022) 'How to Build a JavaFX Application That Does Something', PragmaticCoding, February 6.
         Available at: https://www.pragmaticcoding.ca/javafx/weather (Accessed:11 April 2026).
         */



        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Don't show confirmation message at start
        confirmationLabel.setVisible(false);
        progressIndicator.setVisible(false); //Don't show loading icon at start


        //Changing appearance of combo box to make object displayed more readable
        bookComboBox.setConverter(new StringConverter<BookData>() {
            @Override
            public String toString(BookData bookData) {

                //To avoid null pointer error
                if (bookData != null) {
                    //For each book show the book name + the author
                    return bookData.getBookName() + " by " + bookData.getAuthor();
                } else {
                    return "";
                }
            }

            @Override
            public BookData fromString(String s) {
                return null;
            }
        });

        bookComboBox.setCellFactory(bookDataListView -> new DropdownBookCell());
        addBookButton.setVisible(false);

    }

    //Used to update GUI after thread is complete:
    public void setRunnable(Consumer<Runnable> bookFetcher) {
        this.bookFetcher = bookFetcher;
    }

    public void setEditionsFetcher(Consumer<Runnable> editionsFetcher) {
        this.editionsFetcher = editionsFetcher;
    }

    public void adjustGui(boolean state) {
        searchBookButton.setDisable(state);
        addBookButton.setDisable(state);
        manualToggle.setDisable(state);

        //Show a loading icon
        progressIndicator.setProgress(-1);
        progressIndicator.setVisible(state);
    }


    //Needed to complete query:
    //Give query to other classes to complete search:
    public String getQuery() {
        return query;
    }

    //Return editions query from selected book. To begin 2nd API call.
    public String getEditionQuery() {
        return editionQuery;
    }

    //Pass user's selected book to retrieve edition level info for it:
    public BookData getWork() {
        return selectedBook;
    }


    //Showing search results:

    //Set the combo box with results retrieved from API
    public void setBookComboBox(ObservableList<BookData> books) {
        bookComboBox.setItems(books);
        bookComboBox.show();
    }

    //Use to show user if there are any errors when searching and adding books
    //State true = there was a failure so show red message. Vice versa.
    //Example: Timeout error
    public void setConfirmationLabel(String text, boolean state) {
        confirmationLabel.setText(text);
        if(state){
            confirmationLabel.setTextFill(Color.RED);
        }
        else{
            confirmationLabel.setTextFill(Color.GREEN);
        }
        confirmationLabel.setVisible(true);

    }



    //User presses button to go back
    //Go back to manually adding books page
    public void toggleManual(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/reading/addBook.fxml"));
            Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(Paths.addBookCss).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //User presses back button to go back to reading list
    public void backToReadingList(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/com/example/reading/Controller.fxml"));
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(Paths.readingListCss);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}

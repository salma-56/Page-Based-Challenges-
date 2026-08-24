package com.example.reading.challenge;

import com.example.reading.HomePageController;
import com.example.reading.Paths;
import com.example.reading.randomiser.Controller;
import com.example.reading.readingList.Book;
import com.example.reading.readingList.BookDatabase;
import com.example.reading.user.UserDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


//Class controls retrieving challenge description from GUI and adding to database
//Also creates error messages
public class AddChallengeController implements Initializable {


    //User inputs information to set up challenge
    @FXML
    private TextField challengeNameField;
    @FXML
    private TextField startPageField;

    @FXML
    private TextField endPageField;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ChoiceBox<Book> bookChoiceBox; //Drop-down of all books user wishes to read

    private ObservableList<Book> books;

    //Used for navigation
    @FXML
    private Button addChallenge; //User presses to confirm details

    @FXML
    private Button backButton;

    @FXML
    private Button randomiseButton;

    @FXML
    private Button reset;


    //Used to create error messages:
    @FXML
    private Label challengeNameAsterisk, bookAsterisk, pagesAsterisk, dateAsterisk;

    @FXML
    private Label confirmationLabel, counterLabel;


    //User clicks addChallenge button
    //Retrieve data in fields
    //Make collection class  add values to db
    //Shows confirmation messages to user
    public void addChallenge(ActionEvent event) {

        confirmationLabel.setVisible(false);

        //Failed to add challenge - print error messages
        if (!checkValues()) {
            showErrorLabels();
        }

        //Successfully added challenge:
        else {

            removeLabels();


            confirmationLabel.setText("Added Challenge!");
            confirmationLabel.setTextFill(Color.GREEN);
            confirmationLabel.setVisible(true);
            //Retrieve the user's challenge description:
            String challengeName = challengeNameField.getText();
            int startPage = Integer.parseInt(startPageField.getText());
            int endPage = Integer.parseInt(endPageField.getText());


            //Validate user's input
            if (startPage >= endPage) {
                confirmationLabel.setText("Start page cannot be bigger or equal to end page.");
                confirmationLabel.setTextFill(Color.RED);
                return;
            }

            Book chosenBook = bookChoiceBox.getSelectionModel().getSelectedItem();
            LocalDate endDate = endDatePicker.getValue();

            if (endDate.isBefore(LocalDate.now())) {
                confirmationLabel.setText("End date cannot be set in the past.");
                confirmationLabel.setTextFill(Color.RED);
                return;
            }

            //Pass values to collection class - which creates challenge
            ChallengeList listOfChallenges = new ChallengeList();
            listOfChallenges.addChallenge(LocalDate.now(), endDate, startPage, endPage, challengeName, chosenBook.getBookName(), chosenBook.getBookId());

            //Now includes book name with challenge description to avoid slow performance
        }
    }

    @FXML
    public void randomiseBook(ActionEvent event) {

        confirmationLabel.setVisible(false);
        if (books.size() < 2) {

            confirmationLabel.setText("Randomiser unavailable. Need at least 2 books in reading list!");
            confirmationLabel.setTextFill(Color.RED);
            confirmationLabel.setVisible(true);

            return;
        }


        reset.setVisible(false);
        int userId = HomePageController.userId;

        //Check counter level:
        BookDatabase db = new BookDatabase();
        Connection conn = db.connect("bookapp", "postgres", "oracle");
        UserDatabase user = new UserDatabase(conn);
        int counter = user.retrieveCounter(userId);
        System.out.println("counter is " + counter);

        //Check if counter needs updating
        if (counter == 0) {

            //Check time to update counter:
            LocalDateTime resetTime = user.retrieveCounterResetTime(userId);

            //If time is currently/after the reset time:
            if (!LocalDateTime.now().isBefore(resetTime)) {
                System.out.println("Reset allowed!");
                user.updateCounter(2, userId);  //Reset counter
                randomise();
                System.out.println("New counter is 2");
                counterLabel.setText("Counter: 2");
                confirmationLabel.setVisible(false);
            } else {
                System.out.println("Keep waiting...");


                /*
                w3schools (no date) Java Date and Time. Available at: https://www.w3schools.com/java/java_date.asp
                (Accessed 11 April 2026).

                Used to format date and time
                 */
                DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");


                confirmationLabel.setVisible(true);
                //Add additional minute to reset time for correctness
                confirmationLabel.setText("Randomiser can be used at " + resetTime.plusMinutes(1).format(format));
                confirmationLabel.setTextFill(Color.RED);

            }
        }


        //If have enough counters, do randomiser method
        if (counter > 0) {
            //Do randomise function
            randomise();

            //Reduce counter by 1
            --counter;

            //Store new counter level in database
            user.updateCounter(counter, userId);
            counterLabel.setText("Counter: " + counter);

            //If counter now at 0, then update the log time to create a 1 minutes wait
            if (counter == 0) {
                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime resetCounterTime = currentTime.plusMinutes(1);
                user.updateCounterTime(resetCounterTime, HomePageController.userId);
                counterLabel.setText("Counter: " + counter);
            }
        }


    }

    //Select first two books from list
    public void randomise() {
        FXCollections.shuffle(books);
        Book randomBook1 = books.getFirst();
        Book randomBook2 = books.get(1);

        try {
            new Controller(books, this);
        } catch (Exception e) {
            bookChoiceBox.getItems().clear();
            bookChoiceBox.getItems().add(randomBook1);
            bookChoiceBox.getItems().add(randomBook2);
            bookChoiceBox.show();
            reset.setVisible(true);

        }


    }


    public void reset(ActionEvent event) {
        //Populating choice box so user can see their books:
        BookDatabase db = new BookDatabase();
        Connection conn = db.connect("bookapp", "postgres", "oracle");

        //Populate drop-down list with the books (from the reading list)
        books = FXCollections.observableArrayList(db.readPrepData(conn));
        bookChoiceBox.setItems(db.readPrepData(conn));

    }

    //Checking user's fields.
    //Returns true if user input is valid. Otherwise returns false.
    public boolean checkValues() {

        //If any fields are blank - do not add challenge to database
        if (challengeNameField.getText().isBlank() || startPageField.getText().isBlank() || endPageField.getText().isBlank() || endDatePicker.getValue() == null || bookChoiceBox.getSelectionModel().getSelectedItem() == null) {
            return false;
        }

        //If fields are not blank - can add challenge to database
        return true;

    }


    //Method finds the cause of the error e.g no challenge name was written
    //Result- shows error message
    public void showErrorLabels() {

        //Set all labels to false at start to ensure correctness. E.g no false error messages
        challengeNameAsterisk.setVisible(false);
        pagesAsterisk.setVisible(false);
        bookAsterisk.setVisible(false);
        dateAsterisk.setVisible(false);


        confirmationLabel.setText("* indicates missing fields");
        confirmationLabel.setTextFill(Color.RED);
        confirmationLabel.setVisible(true);

        //For each error, draw the corresponding error message
        if (challengeNameField.getText().isBlank()) {
            errorLabelDescription(challengeNameAsterisk);
        }

        if (startPageField.getText().isBlank() || endPageField.getText().isBlank()) {
            errorLabelDescription(pagesAsterisk);
        }

        if (endDatePicker.getValue() == null) {
            errorLabelDescription(dateAsterisk);
        }

        if (bookChoiceBox.getSelectionModel().getSelectedItem() == null) {
            errorLabelDescription(bookAsterisk);
        }
    }


    //A description of the error message
    //Call method to draw the error message
    public void errorLabelDescription(Label label) {
        label.setText("*");
        label.setTextFill(Color.RED);
        label.setVisible(true);
    }

    public void removeLabels() {
        challengeNameAsterisk.setVisible(false);
        pagesAsterisk.setVisible(false);
        dateAsterisk.setVisible(false);
        bookAsterisk.setVisible(false);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Making all confirmation messages invisible at start
        challengeNameAsterisk.setVisible(false);
        bookAsterisk.setVisible(false);
        pagesAsterisk.setVisible(false);
        dateAsterisk.setVisible(false);

        reset.setVisible(false);


        //Populating choice box so user can see their books:
        BookDatabase db = new BookDatabase();
        Connection conn = db.connect("bookapp", "postgres", "oracle");

        UserDatabase user = new UserDatabase(conn);
        int counter = user.retrieveCounter(HomePageController.userId);
        counterLabel.setText("Counter: " + counter);


        //Populate drop-down list with the books (from the reading list)
        books = FXCollections.observableArrayList(db.readPrepData(conn));
        bookChoiceBox.setItems(db.readPrepData(conn));


        //Used to print out book titles in drop down list, instead of the object.toString value

        /*

        // Source - https://stackoverflow.com/a/52935947
        // Posted by Zephyr, modified by community. See post 'Timeline' for change history
        // Retrieved 2026-04-11, License - CC BY-SA 4.0
        https://creativecommons.org/licenses/by-sa/4.0/deed.en#ref-indicate-changes

         Changes: If condition in the toString function

         */
        bookChoiceBox.setConverter(new StringConverter<Book>() {
            @Override
            public String toString(Book book) {

                if (book != null) {
                    return book.getBookName();
                } else {
                    return "Choose book: ";  //To stop a null pointer. If no choice selected yet- then print this message
                }
            }

            @Override
            public Book fromString(String s) {
                return null;
            }
        });

    }

    public void setBookChoiceBox(Book randomBookSelected) {
        bookChoiceBox.getSelectionModel().select(randomBookSelected);
    }

    //User presses button to cancel/go back to the menu page
    public void backToMenu(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/reading/challengeMenu.fxml"));
            Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(Paths.challengeMenuCss).toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

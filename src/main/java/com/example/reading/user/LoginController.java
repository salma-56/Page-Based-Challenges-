package com.example.reading.user;

import com.example.reading.HomePageController;
import com.example.reading.LoginApplication;
import com.example.reading.challenge.Challenge;
import com.example.reading.challenge.ChallengeDatabase;
import com.example.reading.challenge.ChallengeList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ResourceBundle;


//Use to retrieve user's input and allow them to access home page if correct
//If details incorrect, displays error message instead
public class LoginController implements Initializable {

    //To enter username and password:
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    //For styling login page:
    @FXML
    private Text loginText;

    @FXML
    private AnchorPane anchorTitle;

    @FXML
    private Text loginTitle;

    @FXML
    private AnchorPane loginBox;

    @FXML
    private HBox bulletPoint1, bulletPoint2;

    //Error handling - if details incorrect, show corresponding error message
    @FXML
    private Label confirmationLabel;

    @FXML
    private Label usernameAsterisk, passwordAsterisk;


    //User presses to log in. Validates their details.
    @FXML
    private Button loginButton;



    //Retrieves users input after they press login button
    //If user has missing fields - then display asterisk
    //If user's details incorrect - show error message
    //If user detail is correct, show their home page

    public void login(ActionEvent event) {

        //If user attempts to login again (after failure) remove the error label
        confirmationLabel.setVisible(false);
        usernameAsterisk.setVisible(false);
        passwordAsterisk.setVisible(false);

        //First check if fields are missing - do not accept empty text/spaces
        if(usernameField.getText().isBlank()) {
            System.out.println("Missing username");
            failureAsterisk(usernameAsterisk);
        }

        if(passwordField.getText().isBlank()) {
            failureAsterisk(passwordAsterisk);
            System.out.println("Missing password");
        }

        //If user has inserted information, validate in database
        if(!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {
            String username = usernameField.getText();
            String password = passwordField.getText();



            //If username and password is found, open the home page:
            UserDatabase database = new UserDatabase(LoginApplication.conn);
            int userId = database.checkPassword(username,password);
            HomePageController.userId = userId;


            if(userId != -1) {
                System.out.println("Logged in!");

                //Update challenges so they now have a 'fail' status:
                //Needed so challenges have correct status since some may have failed today.
                updatingChallengeStatus(userId);

                //Go to the home page to use feature
                goToHomePage(event);
            }

            //Login failed: Show error message
            else{
                confirmationLabel.setVisible(true);
                confirmationLabel.setText("Incorrect details");
                confirmationLabel.setStyle("-fx-text-fill: red");
            }

        }



    }


    //If login is successful, show the home page view
    public void goToHomePage(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/com/example/reading/homePage.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(parent);
            scene.getStylesheets().add("/com/example/reading/stylesheets/homePage.css");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }



    //Show red asterisk to indicate missing fields:
    public void failureAsterisk(Label label) {
        label.setVisible(true);
        label.setText("*");
        label.setStyle("-fx-text-fill: red");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Do not show confirmation or error message until user logs in
        confirmationLabel.setVisible(false);
        usernameAsterisk.setVisible(false);
        passwordAsterisk.setVisible(false);

    }





    //Used to find any newly failed challenges
    //If one is found, the status is updated to fail
    //If none is found, no changes made
    //Updates last checked date - ensures challenges are checked once a day
    public void updatingChallengeStatus(int userId) {
        Connection conn = LoginApplication.conn;
        UserDatabase userDb = new UserDatabase(conn);


        //Retrieve the last time the inactive challenges were searched and updated
        LocalDate lastLogDate = userDb.readLastLog(conn, userId);
        System.out.println("Last read was: " + lastLogDate);

        ChallengeDatabase db = new ChallengeDatabase();

        //If there was no recent checks (null)
        // Or there was no check today
        if(lastLogDate == null || lastLogDate.isBefore(LocalDate.now())) {

            //Check how many failed challenges there are:
            int numOfFailedChallenge = db.countFailedChallenges(conn);
            System.out.println("There are " + numOfFailedChallenge + " failed challenges");

            //Updating Challenges:
            if (numOfFailedChallenge < 0) {
                System.out.println("An error"); //Cannot count -ve records so error
            } else if (numOfFailedChallenge == 0) {
                System.out.println("Do nothing"); //No challenges need updating, do nothing
            } else {
                System.out.println("Updating challenges...");
                db.setFailedChallenge(conn); //Found expired challenges, set status to failed.
            }

            //If no log record - insert new value into database
            //Or if record exists update the access date

            userDb.updateLogRecord(conn, userId); //Update log record to today's date
        }

        //The system made an update today. No need to check challenges again.
        else{
            System.out.println("No more updates for today!");
        }

    }


//Go to create a new account page
    public void createAccount(ActionEvent event){
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/com/example/reading/createAccount.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(parent);
            scene.getStylesheets().add("/com/example/reading/stylesheets/createAccount.css");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.example.reading.user;

import com.example.reading.LoginApplication;
import com.example.reading.Paths;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.text.DateFormatter;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

//To create unique username and password
public class CreateAccount implements Initializable {

    @FXML
    private Button login, create;

    @FXML
    private Label usernameAsterisk, passwordAsterisk;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Text dateText;




    public void createAccount(ActionEvent event){

        //New attempt: Hide error messages
        usernameAsterisk.setVisible(false);
        passwordAsterisk.setVisible(false);
        errorLabel.setVisible(false);


        //Show error messages if user left fields empty
        if(usernameField.getText().isBlank()) {
            usernameAsterisk.setVisible(true);
            showErrorLabel();

        }
        if(passwordField.getText().isBlank()) {
            passwordAsterisk.setVisible(true);
            showErrorLabel();
            return;
        }

        //If here, user has entered login details. Validate them next.

        if(!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {


            //Does this username already exist?
            UserDatabase userDb = new UserDatabase(LoginApplication.conn);
            String userExist = userDb.checkUsername(usernameField.getText());
            System.out.println("user exist value " + userExist);

            //Username is unique - go back to login page
            if (userExist == null) {
                System.out.println("new user found!");
                userDb.insertUser(usernameField.getText(), passwordField.getText());
                login(event);

            }

            //Username already in use. Pick another.
            else {
                System.out.println("user exists");
                showUserExist();

            }

        }
    }


    public void showErrorLabel() {
        errorLabel.setText("* indicates missing fields");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(true);
    }

    public void showUserExist(){
        errorLabel.setText("Username already taken. Pick another.");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(true);
    }

    //If successful creation, go to login page
    public void login(ActionEvent event){

        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/com/example/reading/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(Paths.loginCss);
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Hide error labels
        usernameAsterisk.setVisible(false);
        passwordAsterisk.setVisible(false);
        errorLabel.setVisible(false);

        //Appearance: Show date and time on form
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.valueOf(now.getMonthValue());
        String date = String.valueOf(now.getDayOfMonth());

        dateText.setText("On " + date + "/" + month + "/" + year);
    }

}

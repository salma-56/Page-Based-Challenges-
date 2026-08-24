package com.example.reading;

import com.example.reading.challenge.Challenge;
import com.example.reading.challenge.ChallengeDatabase;
import com.example.reading.readingList.BookDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;


//Shows home page, where user can navigate to different features
public class HomePageController  {




    //Buttons to navigate to other features
    @FXML
    private Button openReadingList;

    @FXML
    private Button openChallengeList;

    @FXML
    private Button openTimer;

    @FXML
    private Text contentsPageText;

    //For styling
    @FXML
    private HBox marginBox;

    @FXML
    private Label welcomeText;

    @FXML
    private PieChart chart;

    //Navigating between features
    private Parent parent;
    private Stage stage;
    private Scene scene;


    //Stores id of user who logged in
    public static int userId;

    //User presses reading list to view their books
    @FXML
    protected void openReadingList(ActionEvent event) {

        System.out.println(userId);
        try {
            parent = FXMLLoader.load(getClass().getResource("/com/example/reading/Controller.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource("/com/example/reading/stylesheets/readingList.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

        }
        catch(Exception e) {

            e.printStackTrace();
        }

    }




    //User presses challenge button to view their challenges
    @FXML
    protected void openChallenges(ActionEvent event) {

        try {
            parent = FXMLLoader.load(getClass().getResource("/com/example/reading/challengeMenu.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource(Paths.challengeMenuCss).toExternalForm());
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }


    //User presses open timer
    //Load timer view and its stylesheets
    public void openTimer(ActionEvent event) {

        try{
            parent = FXMLLoader.load(getClass().getResource("/com/example/reading/timer.fxml"));
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource("/com/example/reading/stylesheets/timer.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

//Go back to 'login' page
    public void logout(ActionEvent event){

        try {
            parent = FXMLLoader.load(getClass().getResource("/com/example/reading/login.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource(Paths.loginCss).toExternalForm());
            stage.setScene(scene);
            stage.show();

        }
            catch(Exception e){
                e.printStackTrace();
            }
        }



}

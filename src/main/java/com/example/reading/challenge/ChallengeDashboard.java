package com.example.reading.challenge;

import com.example.reading.Paths;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

//Class will show overview of challenges.
public class ChallengeDashboard implements Initializable {


    //Use to show small window for 2 active challenges
    @FXML
    private Label challengeOverviewName1, challengeOverviewName2;

    @FXML
    private ProgressBar challengeOverviewProgress, challengeOverviewProgress2;

    private Challenge challenge1, challenge2;

    @FXML
    private AnchorPane challengeBoard;

    @FXML
    private Label borderText;

    @FXML
    private VBox book1Box, book2Box;

    @FXML
    private Line line1;

    //Back to home page
    @FXML
    private Button backButton;
    @FXML
    private Button seeActiveChallengesButton, seePastChallengesButton;

    //Use to move switch scene
    private Parent root;
    private Stage stage;
    private Scene scene;


    //Back to home page
    public void backToHomePage(ActionEvent event) {

        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/reading/homePage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/reading/stylesheets/homePage.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //User presses addChallenge
    //Show form to add challenge to database
    public void addChallenge(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/reading/addChallenge.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add("/com/example/reading/stylesheets/addChallenge.css");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //User presses button to view all challenges
    //Switch form to viewing challenges pages
    public void seeChallenge(ActionEvent event) {

        try {
            root = FXMLLoader.load((getClass().getResource("/com/example/reading/activeChallenge.fxml")));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(Paths.activeChallengeCss);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void openPastChallenges(ActionEvent event) {

        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/reading/pastChallenge.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(Paths.pastChallengeCss).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    //Allow user to click challenge to update their progress
    EventHandler handleMouse = new EventHandler() {
        @Override
        public void handle(Event event) {

            try {
                FXMLLoader load = new FXMLLoader(getClass().getResource("/com/example/reading/logChallenge.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(load.load());
                scene.getStylesheets().add(getClass().getResource(Paths.logChallengeCss).toExternalForm());
                LogChallengeController updateChallenge = load.getController();
                updateChallenge.initData(challenge1);
                stage.setScene(scene);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    EventHandler handleMouse2 = new EventHandler() {
        @Override
        public void handle(Event event) {

            try {
                FXMLLoader load = new FXMLLoader(getClass().getResource("/com/example/reading/logChallenge.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(load.load());
                scene.getStylesheets().add(getClass().getResource(Paths.logChallengeCss).toExternalForm());
                LogChallengeController updateChallenge = load.getController();
                updateChallenge.initData(challenge2);
                stage.setScene(scene);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //Show some challenges from database in the overview section
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ChallengeList listOfChallenges = new ChallengeList();
        ObservableList<Challenge> myChallenges = listOfChallenges.retrieveEarliestChallenges("Ongoing"); //Retrieve challenges with earliest deadlines

        borderText.setVisible(false);
        book1Box.setVisible(false);
        book2Box.setVisible(false);

        //Code below populates the challenge dashboard
        //Shows name and progress made for 2 challenges

        //If no challenges then do not show anything in overview window:
        if (myChallenges.isEmpty()) {
            borderText.setVisible(true);
            line1.setVisible(false);
        }

        //If there's 1 challenge, then  show challengeName and progress for first label
        //Keep the other window invisible

        //If 2+ challenge still set window 1 first
        if (!myChallenges.isEmpty()) {
            challenge1 = myChallenges.getFirst();
            challengeOverviewName1.setText(challenge1.getChallengeName());


            double challenge1Progress = challenge1.calculatePercentage(challenge1.getStartPage(), challenge1.getEndPage(), challenge1.getChallengeProgress());
            challengeOverviewProgress.setProgress(challenge1Progress);


            book1Box.setVisible(true);
            book1Box.setOnMouseReleased(handleMouse); //Allow user to click this challenge and show their progress

        }

        //If 2+ challenges, then show challenge name and overview for top two challenges
        if (myChallenges.size() >= 2) {

            challenge2 = myChallenges.get(1);
            challengeOverviewName2.setText(challenge2.getChallengeName());

            double challenge2Progress = challenge2.calculatePercentage(challenge2.getStartPage(), challenge2.getEndPage(), challenge2.getChallengeProgress());
            challengeOverviewProgress2.setProgress(challenge2Progress);

            book2Box.setVisible(true);
            book2Box.setOnMouseReleased(handleMouse2); //Allow user to click second challenge to show its progress
        }
    }



    //User presses button to open statistics page
    public void openStatistics(ActionEvent event){

        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/com/example/reading/statistics.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource("/com/example/reading/stylesheets/statistics.css").toExternalForm());
            stage.setScene(scene);
            stage.show();


        }catch(Exception e) {
            e.printStackTrace();
        }
    }



}







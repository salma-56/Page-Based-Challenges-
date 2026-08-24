package com.example.reading.challenge;

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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class LogChallengeController {


    //Fields to show the challenge description
    @FXML
    private ProgressBar challengeProgress;

    @FXML
    private Label challengeName;

    @FXML
    private Label startPage, endPage;

    @FXML
    private TextField currentPage;

    @FXML
    private Label daysRemaining;

    @FXML
    private Button updateButton;

    @FXML
    private Button backButton;

    @FXML
    private Label confirmationLabel;

    private int challengeID;  //Use to update challenge progress in database

    private int startingPage;  //Need to store to ensure user's updated page is past the initial page.
    //Without this, the user can make -ve progress in a book

    private int pageGoal; //Used to see if a user completed a challenge



    //User inputs their current page
    //Update challenge progress in db
    @FXML
    public void pressUpdateButton(ActionEvent event) {

        confirmationLabel.setVisible(false);

        int newProgress = Integer.parseInt(currentPage.getText());
        ChallengeList listOfChallenges = new ChallengeList();

        //If the page the user is on is same as/exceedes their target page
        //Then challenge is complete
        if(newProgress >= pageGoal) {
            listOfChallenges.completeChallenge(challengeID);
        }

        if(newProgress >= startingPage) { //Cannot read backwards. Without this check, we have -ve progress
            listOfChallenges.updatePage(newProgress, challengeID);
            goBack(event);  //After click update- immediately go back to challenge menu

        }
        else {
            //User cannot read backwards from start page
            System.out.println("Invalid Page number. Current Page cannot be before the starting one.");
            confirmationLabel.setText("Page number must be greater than start page.");
            confirmationLabel.setTextFill(Color.RED);
            confirmationLabel.setVisible(true);
        }



    }

    //User decides to not update the book
    public void goBack(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/reading/activeChallenge.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(Paths.activeChallengeCss).toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //User selects a challenge to update
    //This function retrieves the user's selected challenge
    //And initialises labels to match user's selected challenge

    //Result: User selects challenge and form opens up with the challenge details
    public void initData(Challenge challenge) {


            challengeID = challenge.getChallengeID();  //Will not show up for user. But store challenge Id so user can make updates


            challengeName.setText(challenge.getChallengeName());


            double challengePerc = challenge.calculatePercentage(challenge.getStartPage(), challenge.getEndPage(), challenge.getChallengeProgress());
            challengeProgress.setProgress(challengePerc);

            // challengeProgress.setProgress(challenge.getChallengeProgress());
            startPage.setText(String.valueOf(challenge.getStartPage()));
            endPage.setText(String.valueOf(challenge.getEndPage()));

            //If challenge is due in today, write this. Instead of 0 days remaining
            if (challenge.getDaysRemaining() == 0) {
                daysRemaining.setText("Final Day!");
            } else {
                daysRemaining.setText(String.valueOf(challenge.getDaysRemaining()));
            }

            challengeID = challenge.getChallengeID();  //Will not show up for user. But store challenge id  so user can make updates
            startingPage = challenge.getStartPage();  //Use to ensure user's update their progress correctly
            pageGoal = challenge.getEndPage(); //To see if user completed a challenge
        }


}

package com.example.reading.challenge;

import com.example.reading.Paths;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;


//Used to modify list appearance of active challenges
public class ActiveChallengeCellController extends ListCell<Challenge> {

    //Show challenge progress
    @FXML
    private ProgressIndicator progressIndicator;

    //Show challenge description:
    @FXML
    private Label challengeNameLabel, bookNameLabel, currentPageLabel, challengeDatesLabel, remainingDaysLabel;

    @FXML
    private AnchorPane anchor;

    //Use to set controller
    @FXML
    private FXMLLoader loader;


    //Use method to define appearance of list

    /*
    Johannes (2016) 'Custom ListCell in a JavaFX ListView', Turais, 23 May.
    Available at: https://www.turais.de/how-to-custom-listview-cell-in-javafx/ (Accessed: 11 April 2026).

    Used for the if(empty || challenge == null) block and if(loader==null) block
     */
    @Override
    protected void updateItem(Challenge challenge, boolean empty) {
        super.updateItem(challenge,empty);


//If no challenge - do not set the labels, avoids null pointer error
        if(empty || challenge == null) {
            setText(null);
            setGraphic(null);
        }

        //If there is a challenge- set its appearance
        else {

            if (loader == null) {
                //Setting controller
                loader = new FXMLLoader(getClass().getResource("/com/example/reading/activeChallengeCell.fxml"));
                loader.setController(this);


                try {
                    loader.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            anchor.setStyle("-fx-border-color: black");
            //Defining each label's value:

            challengeNameLabel.setText(challenge.getChallengeName());
            currentPageLabel.setText( "Current page: " +  String.valueOf(challenge.getChallengeProgress()));
            challengeDatesLabel.setText("Pages: " + challenge.getStartPage() + " - " + challenge.getEndPage());
            progressIndicator.setProgress(challenge.calculatePercentage(challenge.getStartPage(), challenge.getEndPage(), challenge.getChallengeProgress()));

            //For active challenges - show days remaining
            if(challenge.getChallengeStatus().equals("Ongoing")) {

                //If challenge is due in today, write this. Instead of 0 days remaining
                if (challenge.getDaysRemaining() == 0) {
                    remainingDaysLabel.setText("Final Day!");
                } else {
                    remainingDaysLabel.setText(String.valueOf("Days remaining: " + challenge.getDaysRemaining()));
                }


            }
            else{
                remainingDaysLabel.setVisible(false); //For past challenges (failed/completed) don't show days remaining. Not needed.

                //Colour background red if challenge was a failure
                if (challenge.getChallengeStatus().equals("Failed")) {
                    anchor.setStyle("-fx-background-color: #f68989 ");

                }

                //Colour background green if completed
                else {
                    anchor.setStyle("-fx-background-color: #bfefba");
                }
            }



            bookNameLabel.setText(challenge.getChallengeBook());




            //Only double click if this is an active challenge:
            //Double click allows you to update the page - should be disabled for past challenges
            if(challenge.getChallengeStatus().equals("Ongoing")) {
                //If you click a challenge 2x, go to the update Challenge page


                /*
                Reference:
                 Source - https://stackoverflow.com/a/10950824
                 Posted by Uluk Biy
                 Retrieved 2026-04-11, License - CC BY-SA 3.0

                 License: https://creativecommons.org/licenses/by-sa/3.0/
                 Used to create the if condition guard for the setOnMouseClicked function
                 */
                setOnMouseClicked(mouse -> {
                    if (mouse.getButton().equals(MouseButton.PRIMARY) && mouse.getClickCount() == 2) {
                        passData(mouse, challenge);
                    }
                });

                anchor.setStyle("-fx-background-color: pink");
            }
        }

            setText(null);
            setGraphic(anchor);

    }

    //User selects a challenge
    //This function passes the challenge information to the next controller - logChallenge
    //The logChallenge controller now knows the selected challenge
    //The controller can ensure the GUI shows details for the selected challenge - e.g challengeName, endDate etc.



   //User selects challenge - show challenge details in new page
    public Stage passData(MouseEvent event, Challenge selectedChallenge) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/reading/logChallenge.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource(Paths.logChallengeCss).toExternalForm());
            stage.setScene(scene);

             /*
            // Source - https://stackoverflow.com/a/14190310
            // Posted by jewelsea, modified by community. See post 'Timeline' for change history
            // Retrieved 2026-04-11, License - CC BY-SA 4.0
            //License : https://creativecommons.org/licenses/by-sa/4.0/deed.en
            Used for the next 4 lines of code.
            */
            LogChallengeController controller = loader.getController();  //Creating controller for updatePage

            controller.initData(selectedChallenge); //Call function in LogChallengeController to give it the selected challenge details
            stage.show(); //Show the update progress page now
            return stage;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}

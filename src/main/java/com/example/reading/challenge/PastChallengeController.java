package com.example.reading.challenge;

import com.example.reading.Paths;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PastChallengeController implements Initializable {

    @FXML
    private ListView<Challenge> pastChallengeList;

    @FXML
    private Text pastChallengeText;

    
    //Used to filter so user can see either failed or completed challenges
    @FXML
    private CheckBox failedChallengeFilter;

    @FXML
    private CheckBox completedChallengeFilter;


    @FXML
    private Button backButton;



    //Used to retrieve challenges from database
    private ChallengeList listOfChallenges;




    //Press back button to load the challenge dashboard/menu again
    public void backToChallengeMenu(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/reading/challengeMenu.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(Paths.challengeMenuCss).toExternalForm());
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {e.printStackTrace();}

    }

    //User clicks checkbox to filter complete challenges only
    //Or User clicks checkbox to remove the filter
    public void filterCompletedChallenges() {
        failedChallengeFilter.setSelected(false); //Disable selecting failedChallenge box

        //User selects filter to see completed challenges
        if(completedChallengeFilter.isSelected()) {

            //Get challenges - show the latest one at the top
            ObservableList<Challenge> completedChallenges = listOfChallenges.retrieveEarliestChallenges("Complete");
            FXCollections.reverse(completedChallenges); //Reverse so most recent challenges are shown first
            pastChallengeList.setItems(completedChallenges);

        }

        //User unticks completed tick box, user does not want to filter by completed challenges anymore
        //Instead show user all challenges
        else{
            pastChallengeList.getItems().clear();
            showAllPastChallenges();
        }


    }

    //User ticks failed challenges button to filter by failed challenges
    //Or User unticks to stop filtering by failed challenges
    public void filterFailedChallenges() {
       completedChallengeFilter.setSelected(false); //Disable choice to see completed challenges

        //Allow user to see failed challenges
       if(failedChallengeFilter.isSelected()) {
           ObservableList<Challenge> failedChallenges = listOfChallenges.retrieveEarliestChallenges("Failed");
           FXCollections.reverse(failedChallenges); //Show recently failed challenges first
           pastChallengeList.setItems(failedChallenges);
       }

       //User unticks 'Failed challenges' - they want to remove filter
       //Stop showing filtered challenges, instead show all challenges again
       else{
           pastChallengeList.getItems().clear();
           showAllPastChallenges();
       }


    }


    //Show all challenges - completed and failed ones.
    private void showAllPastChallenges() {
        ObservableList<Challenge> pastChallenges = listOfChallenges.retrievePastChallenge();
        pastChallengeList.setItems(pastChallenges);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Show all past challenges in order of end date
        listOfChallenges = new ChallengeList();
        ObservableList<Challenge> pastChallenges = listOfChallenges.retrievePastChallenge();
        pastChallengeList.setItems(pastChallenges);

        //Used to style the list. Green for completed challenges, red for failed challenges.
        pastChallengeList.setCellFactory(pastChallenge -> new ActiveChallengeCellController());

    }
}

package com.example.reading.challenge;

import com.example.reading.Paths;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

//Shows a list of all active challenges
public class ActiveChallengeController implements Initializable {

    @FXML
    private ListView<Challenge> activeChallengeList;

    @FXML
    private Button backButton;

    @FXML
    private Button removeChallengeButton, updateButton;


    public void removeChallenge(ActionEvent event) {
        //If nothing in challenges list or the user did not select any challenges.
        //Do nothing when button is pressed
        if(activeChallengeList.getItems().isEmpty()  || activeChallengeList.getSelectionModel().isEmpty()) {
            System.out.print("No challenges to remove - button does nothing");
        }

        //Else, delete challenge from GUI and database
        else{
            Challenge selectedChallenge = activeChallengeList.getSelectionModel().getSelectedItems().getFirst();

            activeChallengeList.getItems().remove(selectedChallenge);
            activeChallengeList.refresh();


            //Delete from database
            ChallengeList listOfChallenges = new ChallengeList();
            listOfChallenges.removeChallenge(selectedChallenge.getChallengeID());


        }
    }

    //User presses back to go to the challenge dashboard
    public void backToMenu(ActionEvent event) {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("/com/example/reading/challengeMenu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(Paths.challengeMenuCss).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Press button to go to challenge and make updates
    public void updateChallenge (ActionEvent event) {

        try{

            Challenge selectedChallenge = activeChallengeList.getSelectionModel().getSelectedItem();

            if(selectedChallenge != null) {
                FXMLLoader load = new FXMLLoader(getClass().getResource("/com/example/reading/logChallenge.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(load.load());
                scene.getStylesheets().add(Paths.logChallengeCss);

                // Source - https://stackoverflow.com/a/14190310
                // Posted by jewelsea, modified by community. See post 'Timeline' for change history
                // Retrieved 2026-04-11, License - CC BY-SA 4.0
                //Link: https://creativecommons.org/licenses/by-sa/4.0/deed.en

                //Used reference for the initData call. Changes are the class and object names.
                LogChallengeController controller = load.getController();
                controller.initData(selectedChallenge);
                stage.setScene(scene);
                stage.show();
            }



        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    //When active challenge list is opened
    //Display all active challenges
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ChallengeList listOfChallenges = new ChallengeList();
        ObservableList<Challenge> challenges = listOfChallenges.retrieveEarliestChallenges("Ongoing"); //Show challenges due in the soonest first

        activeChallengeList.setItems(challenges);

        activeChallengeList.setCellFactory(cell -> new ActiveChallengeCellController());
    }

}

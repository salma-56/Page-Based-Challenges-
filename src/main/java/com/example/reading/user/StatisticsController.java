package com.example.reading.user;

import com.example.reading.Paths;
import com.example.reading.challenge.Challenge;
import com.example.reading.challenge.ChallengeList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

//Show stats for challenges
public class StatisticsController implements Initializable {

    @FXML
    private PieChart pieChart, activePieChart;

    @FXML
    private Button backButton;

    private PieChart.Data failedSector;

    private PieChart.Data completeSector;

    @FXML
    private Label pieChartLabel;

    @FXML
    private Label pieChart2Label;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        pieChartLabel.setVisible(false);
        pieChart2Label.setVisible(false);

        //Get number of passed and failed challenges:
        ChallengeList listOfChallenges = new ChallengeList();

        ObservableList<Challenge> failedChallenge = FXCollections.observableArrayList();
        failedChallenge = listOfChallenges.retrieveEarliestChallenges("Failed");

        ObservableList<Challenge> completedChallenge = FXCollections.observableArrayList();
        completedChallenge = listOfChallenges.retrieveEarliestChallenges("Complete");

        int failedCount = failedChallenge.size();
        int completeCount = completedChallenge.size();



        //Do not show pie chart if there are no past challenges
        if (failedCount == 0 && completeCount == 0) {
            pieChart.setVisible(false);
            pieChartLabel.setText("Create challenge to see your statistics!");
            pieChartLabel.setVisible(true);
        } else {

            //Create sector name and size
            failedSector = new PieChart.Data("Failed", failedCount);
            completeSector = new PieChart.Data("Completed", completeCount);


            //Show number of completed and failed challenges
            ObservableList<PieChart.Data> pieChartList = FXCollections.observableArrayList();
            pieChartList.add(failedSector);
            pieChartList.add(completeSector);


            pieChart.setData(pieChartList);
            pieChart.setLegendVisible(false);

        }


        //Second pie chart - Show active challenges
        ObservableList<Challenge> ongoingList = listOfChallenges.retrieveEarliestChallenges("Ongoing");

        int activeCount = ongoingList.size();

        //Do not show anything if 0 active challenges
        if (activeCount == 0) {
            activePieChart.setVisible(false);

            pieChart2Label.setText("Create challenges to see your statistics!");
            pieChart2Label.setVisible(true);
            return;
        }

        //Only display pie chart if there are active challenges:


        ObservableList<PieChart.Data> allData = FXCollections.observableArrayList();



        PieChart.Data activeSector = new PieChart.Data("Active", ongoingList.size());
        PieChart.Data failedSector = new PieChart.Data("Failed", failedCount);
        PieChart.Data completedSector = new PieChart.Data("Completed", completeCount);

        allData.add(failedSector);
        allData.add(completedSector);
        allData.add(activeSector);

        activePieChart.setData(allData);
       activePieChart.setLegendVisible(false);

    }


    //User presses back to go to homepage
    public void goBack(ActionEvent event) {

        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/com/example/reading/challengeMenu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource(Paths.challengeMenuCss).toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


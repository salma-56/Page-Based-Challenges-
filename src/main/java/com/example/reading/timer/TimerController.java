package com.example.reading.timer;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

//Class responsible for creating timer
//Use chooses time and countdown begins
public class TimerController implements Initializable {

    //Display time remaining
    @FXML
    private Label minutesLabel;

    @FXML
    private Label secondsLabel;

    //To start timer
    @FXML
    private Button startButton;

    @FXML
    private Button enterButton;

    //To choose start time
    @FXML
    private Slider timeSlider;

    private int startTime;

    //Choose start page
    @FXML
    private TextField startPageField;

    //Timer
    private Timeline timer;

    //Navigation
    @FXML
    private Button backButton;

    //Represents stars on gui
    @FXML
    private VBox v1, v2, v3, v4, v5, v6, v7, v8, v9, v10;

    //To store each 'star' to show animation
    @FXML
    private ArrayList<VBox> stars;

    @FXML
    private Label paceLabel;

    private int startPage;
    private int endPage;


    //User presses start to begin timer
    public void startTimer(ActionEvent event) {
        System.out.println("Start");

        if (!startPageField.getText().isBlank()) {
            startPage = Integer.parseInt(startPageField.getText());
        }
        //Don't start time if user hasn't chosen time
        if (startTime == 0) {
            System.out.println("Choose start time");
            return;
        }
        //Remove all other GUI components
        startPageField.setVisible(false);
        startButton.setVisible(false);
        timeSlider.setVisible(false);
        paceLabel.setVisible(false);


        LocalDateTime current = LocalDateTime.now();
        LocalDateTime finish = LocalDateTime.now().plusMinutes(startTime);
        System.out.println("Finish time is " + finish);
        long minutes = Duration.between(current, finish).toMinutes();
        System.out.println("Minutes to do " + minutes);


        //Show new changes every second

        //Need new timer if user wants to do a second attempt
        timer = new Timeline();
        //Using JavaFx duration instead of Java's
        timer.getKeyFrames().add(new KeyFrame(javafx.util.Duration.seconds(1), actionEvent -> {
            displayStars();

            LocalDateTime currentTime = LocalDateTime.now();
            Duration remaining = Duration.between(currentTime, finish);
            long sec = remaining.getSeconds() % 60;
            long min = remaining.toMinutes();


            //When timer hits -ve seconds, stop it
            if (sec < 0 && min == 0) {
                startPageField.clear();
                startPageField.setVisible(true);

                //Call method to deal with pages
                startPageField.setPromptText("End Page");

                enterButton.setVisible(true);

                timer.stop();
            }

            //If timer is ongoing, display current time
            //1:00    //Accept 0 seconds
            //0:00 //But not both
            //00:59  //Accept 0 minutes
            if (sec >= 0 && min >= 0) {


                if (sec >= 10) {
                    secondsLabel.setText(String.valueOf(sec));
                } else {

                    secondsLabel.setText("0" + String.valueOf(sec));
                }


                if (min >= 10) {
                    minutesLabel.setText(String.valueOf(min));
                } else {
                    minutesLabel.setText("0" + String.valueOf(min));
                }


            }
        }));

        //Begin timer
        timer.setCycleCount(Animation.INDEFINITE); //Without, timer remains stuck
        timer.play();


    }

    //Randomly choose whether to show each star or not
    public void displayStars() {
        Random random = new Random();
        boolean state;

        //Go through each 'star' and set visibility on/off
        for (VBox b : stars) {
            state = random.nextBoolean();
            b.setVisible(state);   //If false - no star shown. Vice versa.
        }
    }


    //Calculate reading pace if user inputted their start and end page
    //Reading pace units are pages read per minute
    public void readingPace(ActionEvent event) {
        int pagesRead;
        int sessionTime = startTime;
        double readingPace;

        //User can leave field to enter end page as blank
        if (!startPageField.getText().isBlank()) {
            endPage = Integer.parseInt(startPageField.getText());
        }



        //User did not input the start and/or pages
        //Or user inputted an invalid page number
        //Return - cannot calculate reading pace
        if (startPage <= -1 || endPage <= -1) {
            showGuiComponents();
            return;
        }

        //Calculate pages read per minute
        //Call method to show GUI components
        if (startPage < endPage) {
            pagesRead = endPage - startPage;
            readingPace = (double) pagesRead / sessionTime; //Pages read per minute
            paceLabel.setText("Reading pace: " + readingPace + " pages per minute");

            showGuiComponents();
            paceLabel.setVisible(true);
            return;
        }

        //If user is still on same page when timer is up
        //Do not want pagesRead to be 0 - keep at 1 page
        if (startPage == endPage) {
            readingPace = 1 / sessionTime;
            paceLabel.setText("Your reading pace is " + String.valueOf(readingPace));
            showGuiComponents();
        }
    }

    //Timer complete - show all GUI components again
    //So user can begin a new timer
    public void showGuiComponents() {

        enterButton.setVisible(false);
        startTime = 0;
        timeSlider.setVisible(true);

        //Reset text field so user can enter their start page again
        startPageField.clear();
        startPageField.setPromptText("Start page");
        startPageField.setVisible(true);

        startButton.setVisible(true);
        enterButton.setVisible(false);

        //Reset the page information
        startPage = -1;
        endPage = -1;
    }


    //User presses back button to go back to homepage
    //Display home page
    //Stops timer if its playing
    public void goBack(ActionEvent event) {

        //If timer is on, stop it from running
        timer.stop();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/reading/homePage.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add((getClass().getResource("/com/example/reading/stylesheets/homepage.css")).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stars = new ArrayList<>();
        timer = new Timeline();
        enterButton.setVisible(false);

        startTime = 0; //No time set by user yet

        //User has not read any pages yet
        startPage = -1;
        endPage = -1;

        //Populating list with the gui elements
        stars.add(v1);
        stars.add(v2);
        stars.add(v3);
        stars.add(v4);

        stars.add(v5);
        stars.add(v6);
        stars.add(v7);
        stars.add(v8);
        stars.add(v9);
        stars.add(v10);

        //User moves slider to desired time
        //Time is displayed in the GUI
        //number is the value user was originally at eg. 0
        //t1 is the number the user is currently on
        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

                //Slider number represents start time for timer
                startTime = t1.intValue();

                //Previously - Only display 10-minute intervals e.g 0/10/20...60 minutes
                if (startTime % 10 == 0 && startTime != 0) {
                    minutesLabel.setText(String.valueOf(t1.intValue())); //Reflect time on GUI
                }

                //For demonstration - show all minutes
                else{
                    if(startTime < 10) {
                        minutesLabel.setText("0" + String.valueOf(startTime));
                    }
                    else {
                        minutesLabel.setText(String.valueOf(startTime));
                    }
                }
            }
        });


    }
}

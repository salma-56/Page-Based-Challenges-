package com.example.reading;

import com.example.reading.api.ApiController;
import com.example.reading.challenge.ChallengeDatabase;
import com.example.reading.readingList.BookDatabase;
import com.example.reading.user.UserDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

//Sets up database and shows login page
public class LoginApplication extends Application {


    public static Connection conn;

    @Override
    public void start(Stage stage) throws Exception {


        //Connect to database
        BookDatabase db = new BookDatabase();
       conn = db.connect("bookapp", "postgres", "oracle");

        //Create user table
        UserDatabase userDb = new UserDatabase(conn);
        userDb.createUserTable(conn);

        //Create book table
        db.createTable(conn);

        //Create challenge table
        ChallengeDatabase challengeDb = new ChallengeDatabase();
        challengeDb.createTable(conn);




        //Open log in page:
        try{
            Parent parent = FXMLLoader.load(getClass().getResource("/com/example/reading/login.fxml"));
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource("/com/example/reading/stylesheets/login.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

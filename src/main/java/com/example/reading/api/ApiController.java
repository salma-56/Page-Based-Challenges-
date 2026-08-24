package com.example.reading.api;

import com.example.reading.Paths;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/*
REFERENCE: Look up 'ApiControllerMarkup'. Can be found in this 'api' package.
*/

//Used to create threads to begin API calls and handle failures
public class ApiController {


    private BookInteractor bookInteract; //To invoke API
    private AddBookAPIController controller; //To show response to the user
    private FXMLLoader loader; //To load the gui view


    //Loading the view for 'Add Book'
    //Need to load this way to set the Runnable
    public ApiController(Stage stage) {
        try {

            //Load the view so user can search for book
            loader = new FXMLLoader(getClass().getResource("/com/example/reading/addBookApi.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource(Paths.addBookApiCss).toExternalForm());
            stage.setScene(scene);
            controller = loader.getController();

            //Set runnable so user can understand when the api responds
            //E.g loading icon stops displaying


            controller.setRunnable(this::getBookSearches);
            controller.setEditionsFetcher(this::getEditionSearch);
            bookInteract = new BookInteractor(controller);

            stage.show();

        } catch (IOException e) {

            //Show page unvailable message and go to manual?
            throw new RuntimeException(e);
        }

    }


    //User types their book name
    //Call API to find books with the same title
    //If successful, show the book results to the user
    //If failed, let user know to try to search for a book later.
    public void getBookSearches(Runnable gui) {


        //Calls the API to receive search results
        Task<Void> fetchTask = new Task<>() {

            //User searched for book
            //Do API call to retrieve book results
            @Override
            protected Void call() {
                System.out.println("Doing thread call");
                bookInteract.getBookResults(controller.getQuery()); //Pass the user's search query to the API
                System.out.println("done with thread ");
                return null;
            }
        };


        //Once results are received from API. Call method to update GUI.
        fetchTask.setOnSucceeded(event -> {
            bookInteract.showBookResults(); //Handles the gui response for successful + unsuccessful responses
            System.out.println("Shown book results");
            gui.run(); //Run code to re-enable the buttons under AddBookAPIController class
        });


        //If thread/API call fails then tell user.
        fetchTask.setOnFailed(event -> {
            System.out.println("Failed API Search Thread");
            controller.setConfirmationLabel("Please try again later.", true);
            gui.run(); //Re-enable buttons so user can try again
        });



        //Start thread
        Thread fetchThread = new Thread(fetchTask);
        fetchThread.start();

    }


    //Handles second API call
    //User selects book, retrieve the edition information e.g page number
    public void getEditionSearch(Runnable editions) {

        //API call to retrieve editions information for the user's book selection
        //Should retrieve page numbers, cover image
        Task<Void> fetchEditions = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Editions thread");
                bookInteract.getEditionInformation(controller.getEditionQuery(), controller.getWork());
                System.out.println("Finished getting editions");
                return null;
            }


        };


        //If API successful, then call method to update GUI with API response
        fetchEditions.setOnSucceeded(workerStateEvent -> {
                System.out.println("Ediitons thread success");
        bookInteract.showEditionInformation();
        editions.run(); //Re-enable buttons and stop showing loading icon
        System.out.println("Re-enabled button"); });


        //Has not occurred yet but safety precaution.
        //If API fails to retrieve editions information then re-enable add button
        //Tell user to try again later
        fetchEditions.setOnFailed(workerStateEvent ->  {
                    System.out.println("Editions thread failed");
                    editions.run(); //Re-enable add button
                    controller.setConfirmationLabel("Failure retrieving the edition. Try again later.", true);

                });

        //Start thread
        Thread fetchEdition = new Thread(fetchEditions);
        fetchEdition.start();

    }
}




//The marked down version of file used to help create the ApiController class.
//The original file can be found here:
/*https://github.com/PragmaticCoding/WeatherFX/blob/master/src/main/java/ca/pragmaticcoding/weather/WeatherController.java*/
/*

//REMOVED IMPORTS
package ca.pragmaticcoding.weather;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;

//ADDED OWN IMPORTS
import com.example.reading.Paths;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

//NAMES WERE REMOVED AND ADJUSTED TO OWN FUNCTION + CLASS NAMES
public class WeatherController {

    private final WeatherInteractor interactor;

    //REMOVED THE WEATHERVIEWBUILDER CLASS
    private final WeatherViewBuilder viewBuilder;

//ADDED THE STAGE PARAMETER
    public WeatherController(Stage Stage) {

    //ADDED TRY BLOCK TO LOAD VIEW
     try {

            //Load the view so user can search for book
            loader = new FXMLLoader(getClass().getResource("/com/example/reading/addBookApi.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource(Paths.addBookApiCss).toExternalForm());
            stage.setScene(scene);
            controller = loader.getController();}



    //REMOVED THE WEATHERMODEL CLASS
        WeatherModel viewModel = new WeatherModel();


        interactor = new WeatherInteractor(viewModel);

        //REMOVED WEATHERVIEWBUILDER CLASS
        viewBuilder = new WeatherViewBuilder(viewModel, this::fetchWeather);

        //ADDED SETTERS INSTEAD
           controller.setRunnable(this::getBookSearches);
           controller.setEditionsFetcher(this::getEditionSearch);
    }

//RENAMED TO GETBOOKSEARCHES. PARAMETER IS RENAMED TO 'gui'
    private void fetchWeather(Runnable postFetchGuiStuff) {
        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() {

            //REMOVED METHOD
                interactor.checkWeather();

                //ADDED DIFFERENT METHOD AND PRINT STATEMENTS TO CHECK
                System.out.println("Doing thread call");
                bookInteract.getBookResults(controller.getQuery()); //Pass the user's search query to the API
                System.out.println("done with thread ");

                return null;
            }
        };
        fetchTask.setOnSucceeded(evt -> {

        //REMOVED METHODS
            interactor.updateWeatherModel();
            postFetchGuiStuff.run();

        //ADDED OWN METHOD TO SHOW BOOKS IN COMBO BOX
            bookInteract.showBookResults(); //Handles the gui response for successful + unsuccessful responses
            gui.run();

        });


        ADDED THREAD FAILURE PROCEDURE
        fetchTask.setOnFailed(event -> {
            System.out.println("Failed API Search Thread");
            controller.setConfirmationLabel("Please try again later.", true);
            gui.run(); //Re-enable buttons so user can try again
        });


        Thread fetchThread = new Thread(fetchTask);
        fetchThread.start();
    }



//REMOVED FUNCTION
    public Region getView() {
        return viewBuilder.build();
    }


    //ADDED  NEW FUNCTION LISTED BELOW:

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





 */
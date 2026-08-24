package com.example.reading.api;

import com.example.reading.readingList.BookDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONObject;

import java.sql.Connection;

//Calls API and receives response
//Updates GUI according to response
public class BookInteractor {

    //To call the API
    private BookApiCaller caller;

    //To update GUI
    private AddBookAPIController controller;

    //To update GUI if api has error
    private boolean apiError=false;
    private String errorCode;

    public BookInteractor(AddBookAPIController controller) {
        caller = new BookApiCaller();
        this.controller = controller;
    }

    //Contains edition information for user's book
    private BookData editionData;


    //Calls method to begin API call
    //If successful, list is populated with the books the API responded with
    public void getBookResults(String query) {

        System.out.println("Interactor calling api... getbookresults");
        BookData.bookResults.clear(); //Clear books from previous searches


        //Call the API - receives a JSONObject containing response
        JSONObject apiResponse = caller.searchBookQuery(query);

        //If api responds, check for error code
        if(apiResponse!=null) {

            try {
                errorCode = (String) apiResponse.get("code");
                apiError = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //If response is null, then there was a timeout error
        else {
            apiError = true;
        }


        //If api responds and there is no error code, then api responded successfully
        //Call method to read the books found inside api's response
        if(apiResponse!=null && errorCode==null) {
            System.out.println("Calling method to populate combo box...");
            caller.readBookResults(apiResponse);
        }



}

//Updates GUI with api's results
public void showBookResults() {

    //List contains book results retrieved from API
    ObservableList<BookData> books = FXCollections.observableArrayList(BookData.bookResults);

    //If no api-error
    if(!apiError) {

        //Show the User their book results
        if (!books.isEmpty()) {
            controller.setBookComboBox(books);
        }

        //If there are no books found in list, then inform user
        else {
            controller.setConfirmationLabel("No books found.", true);
        }
    }

    //If api has error
    else {

        //If api responded - read error code if available
        if (errorCode!=null && errorCode.equals("503")) {
            controller.setConfirmationLabel("Error using service. Please add book manually or try again later.", true);
        }

        //If no error code- then api has not responded due to timeout
        else {
            controller.setConfirmationLabel("Timeout occurred. Please try again later.", true);
        }
    }

    //Reset error flag for future searches
    apiError=false;
    errorCode = null; //Reset error code for next attempt

}


//Call API to retrieve edition information for user's chosen book
public void getEditionInformation(String query, BookData work) {
    JSONObject edition = caller.searchBookQuery(query);
    System.out.println(query);

    //Object contains API response
    editionData = caller.selectedBookInformation(edition, work);
}


//Storing the edition information for user's chosen book
public void showEditionInformation() {
    System.out.println("Showing edition results");

    //If api responded successfully, store the user's book in the database - if it doesn't exist already
    if (editionData != null) {


        //Show user a success message

        String editionDesc = editionData.getBookName() + " by " + editionData.getAuthor();


        //Add book to database
        String bookName = editionData.getBookName();
        String author = editionData.getAuthor();
        int totalPages = editionData.getTotalPages(); //Default is -1
        String coverPath = editionData.getCoverUrl();

        BookDatabase database = new BookDatabase();
        Connection conn = database.connect("bookapp", "postgres","oracle");

        //Check if book exists - if not, store
        String check = database.checkBook(conn, bookName, author);
        System.out.println("check is " + check);


        if(check == null) {
            System.out.println("Book not in library");
            database.apiInsert(conn, bookName, author, totalPages, coverPath);
            controller.setConfirmationLabel("Added " + editionDesc, false);
//            controller.setImage(editionData);
        }

        else{
            System.out.println("No book");
            controller.setConfirmationLabel("Book is already in reading list!", true);
        }
    }

    //api did not respond. Show error
    else{
        controller.setConfirmationLabel("Error, please try again later.", true);
    }


}
}

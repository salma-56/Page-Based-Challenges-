package com.example.reading.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

//Responsible for doing the actual API calls
//Api call 1: Get books which match user's query
//Api call 2: Get edition information for particular book
public class BookApiCaller {


    public BookApiCaller() {
    }


    //Takes user query and does API call to search for a book/particular edition
    //Returns response if successful. Null if unsuccessful
    public JSONObject searchBookQuery(String query) {

        System.out.println("Doing api call rn......");
        OkHttpClient newClient = new OkHttpClient.Builder()


                /*
                Woyke, K.(2024) A Quick Guide to Timeouts in OkHttp. Available at: "https://www.baeldung.com/okhttp-timeouts" (Accessed: 11 April 2026).
                 */
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(query)
                .build();
        System.out.println("Made Request!");

        //If successful response, return the JSONObject containing response
        try {



            /*
            Kensoft PH (2024) How to connect to an API using JavaFX | Dog API. 24 May.
            Available at: https://www.youtube.com/watch?v=YZeukdxP8pc (Accessed: 11 April 2026).

            Used reference above to generate response string.
             */
            Response response = newClient.newCall(request).execute();
            String responseString = response.body().string();
            JSONObject jsonResponse = checkResponse(responseString);
            return jsonResponse;

        }
        //If Api error occurred e.g timeout/server down. Return null
        catch (Exception e) {
            e.printStackTrace();
            return null; //If there's a timeout, return null object
        }

    }


    //Checks the api's response
    //If invalid do not create object, return null
    //If valid - create jsonObject to read results



    /*
    Khandelwal, N. (2025) Resolving Jsonexception: Jsonobject Text Must Begin With '{' in Java.
    Available at: https://www.baeldung.com/java-jsonexception-jsonobject-text-must-begin-with-curly-brace (Accessed: 11 April 2026).

    Used to create if conditions to check for valid json response
     */
    public JSONObject checkResponse(String response) {


        if (response != null && !response.trim().isEmpty()) {
            System.out.println("Response is not empty.");
        }

        //If response is empty - stop here.
        else {
            System.out.println("Response is empty");
            return null;
        }

        System.out.println("Response " + response);
        //If here, response is not empty. Check format

        //Response is not empty and in correct format. Return response.
        if (response.trim().startsWith("{")) {
            System.out.println("The correct format");
            JSONObject jsonResponseObject = new JSONObject(response);
            return jsonResponseObject;
        }

        //If here, string is not empty.
        //But it does not begin with { so response is invalid. E.g 503 error
        else {
            System.out.println("String not empty, but incorrect format.");
            System.out.println(response);
            JSONObject error = new JSONObject(response); //If here, then API responsed with error code
            return error;
        }


    }


    //Reading results from first API call
    //Results contain books which match user's query
    //Encapsulate each book result in BookData object
    public void readBookResults(JSONObject jsonObject) {

        System.out.println("Reading api's response to retrieve books in apicaller....");
        BookData bookInformation = new BookData();


        //Checking how many books shown for user's query
        int numberOfBooksFound = (int) jsonObject.get("numFound");

        //No books - no matching books found for user's query. E.g user mistyped book name.
        if (numberOfBooksFound == 0) {
            System.out.println("No results found.");
            return;
        }

        //If here, book(s) found matching user's query
        JSONObject booksObject;
        int counter = 0;

        //If more than 10 results found, just show the first 10
        if (numberOfBooksFound >= 10) {
            counter = 10;
        }
        //If less than 10 results found, show them all.
        else {
            counter = numberOfBooksFound;
        }

        //For each book inside the result
        //Call method to encapsulate data in BookData object
        for (int i = 0; i < counter; ++i) {
            booksObject = (JSONObject) ((JSONArray) jsonObject.get("docs")).get(i);
            bookInformation = bookEditionInformation(booksObject);
        }


    }

    //Called for each book found inside the API response
    //Takes in the individual book returned as jsonObject

    public BookData bookEditionInformation(JSONObject booksObject) {

        System.out.println("Read api book...getting edition paths in api caller");
        JSONArray editionsDocsArray = (JSONArray) ((JSONObject) booksObject.get("editions")).get("docs");
        JSONObject editionsObject = (JSONObject) editionsDocsArray.get(0);


        //Try to retrieve book title, author and editions url for the book
        try {
            String editionPath = (String) editionsObject.get("key");
            String bookName = (String) editionsObject.get("title");
            String authorName = (String) ((JSONArray) booksObject.get("author_name")).get(0);

            System.out.println("Book name " + bookName);
            System.out.println("Edition path " + editionPath);
            System.out.println("Author name" + authorName);

            //Create object containing all three values
            return new BookData(bookName, authorName, editionPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            //If book is missing information, do not create object
        }

    }


    //Reads the response for second API call - retrieving edition level information
    //work is the user's selected book
    public BookData selectedBookInformation(JSONObject editionObject, BookData work) {

        //Create object to store edition information
        BookData edition = new BookData();

        //Error handling - only continue if book is not null
        if (work == null) {
            System.out.println("Null selected book");
            return null;
        }

        //Error handling - ensuring api response is not null
        if (editionObject == null) {
            System.out.println("Null edition object");
            return null;
        }

        //If here, neither parameter is null
        else {
            System.out.println(editionObject);

            //Retrieve the user's selected book and get its name + author information
            //Must do this as the api response does not include author name
            edition.setBookName(work.getBookName());
            edition.setAuthorName(work.getAuthor());

            //Try to get edition information from api response:

            //Try to retrieve number of pages
            try {
                int numberOfPages = (int) editionObject.get("number_of_pages");
                System.out.println("Pages: " + numberOfPages);
                edition.setTotalPages(numberOfPages);  //Set total pages in edition object

            } catch (JSONException e) {
                System.out.println("No page number for this edition");
            }

            //Try to receive cover image url
            //If successful, store in object
            try {
                String editionKey = (String) editionObject.get("key");
                String[] path = editionKey.split("/");
                String imageCode = path[2];
                edition.setCoverUrl(imageCode);
            } catch (Exception e) {
                System.out.println("No cover image");
            }

            //Return edition object which may/may not include a page number or cover url
            return edition;
        }


    }
}




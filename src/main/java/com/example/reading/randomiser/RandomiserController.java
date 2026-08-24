package com.example.reading.randomiser;

import com.example.reading.readingList.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

//Displays 2 books in new window
//Allow user to choose either book for their challenge
public class RandomiserController  {

    //Book 1 description
    @FXML
    private Label bookName1, authorName1, totalPage1;

    //Book 2 description
    @FXML
    private Label bookName2, authorName2, totalPage2;

    @FXML
    private Button addBook1, addBook2;

    @FXML
    private ImageView bookCover1, bookCover2;


    //Book 2 information stored here:
    @FXML
    private HBox bookHbox1, bookHbox2;

    //Stores all book information:
    @FXML
    private HBox outerBox;

    private ObservableList<Book> books;

    private Book book1;
    private Book book2;
    private Controller controller;



    public void checkBooks(ObservableList<Book> books, Controller controller) {

        this.controller = controller; //Need controller to pass selected book data back to the 'add challenge' form
        this.books = books;


        if(books == null) {
            System.out.println("IN RANDOMISER NULL");
        }
        if(books.isEmpty()) {
            System.out.println("Randomiesr is empty");
        }

        //Show details for first book
        if(!books.isEmpty()) {
            book1 = books.getFirst();
            showBook(books.getFirst(), bookName1, authorName1, totalPage1, bookCover1);
        }


        //Show second book details
        if(books.size() >= 2) {
            book2 = books.get(1);
            showBook(books.get(1), bookName2, authorName2, totalPage2, bookCover2);

        }
    }

    //Updates GUI to show relevant book information
    //Parameters include labels to update
    public void showBook(Book book, Label bookName, Label authorName, Label totalPage, ImageView bookCover) {
        System.out.println("HI");
        bookName.setText(book.getBookName());
        authorName.setText(book.getAuthorName());


        if(book.getTotalPages() > 0) {
            totalPage.setText(String.valueOf(book.getTotalPages()));
        }

        else{
            totalPage.setVisible(false);
        }

        if(book.getCover() != null) {
            getPhoto(book.getCover(), bookCover);
        }
        else{
            bookCover.setVisible(false);
        }
    }


    /*
OpenAI (2026) AI-generated code by ChatGPT with prompt 'Hello, my image is not loading in javafx. the image can load fine when its inside the folder but not a http url i check for all errors e.g iserror and this is fine imageview can set images since i tried with the image under my folders The actual url connection is not working. when fileinput stream is used an exception is made. the url itself is perfectly fine and can be checked on browsers. how can i get theis url input stream to work', 30 March.
Available at: https://docs.google.com/document/d/1oMNqvWGTxANUIyqK-UsE-DhE4hOdk2Q_aAre565wb_E/edit?usp=sharing (Accessed: 11 April 2026).

     Used for function below to get image
     */
    public void getPhoto(String coverUrl, ImageView bookCover) {
        try{

            String imageUrl = "https://covers.openlibrary.org/b/olid/"
                    + coverUrl + "-M.jpg";
            InputStream inputStream = new URL(imageUrl).openStream();
            Image image = new Image(inputStream);
            bookCover.setImage(image);
            System.out.println("showing");


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot show image");
        }
    }


    //To choose a book for the challenge
    public void selectBook1(ActionEvent event) {
       updateGui(book1, event);
    }

    public void selectBook2(ActionEvent event){
        updateGui(book2,event);
    }

    //Show book on GUI and close stage
    public void updateGui(Book book, ActionEvent event) {
        controller.getSelectedBook(book);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
    }
}

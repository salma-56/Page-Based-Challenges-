package com.example.reading.readingList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.InputStream;
import java.net.URL;

//Use class to adjust how books are display in the reading list
/*
Reference: Check 'BookListViewCellMarkup' in reading list package
*/
public class BookListViewCell extends ListCell<Book> {

    @FXML
    private Label bookName;

    @FXML
    private Label authorName;

    @FXML
    private AnchorPane anchorPane;

    private FXMLLoader mLoader;




    @Override
    protected void updateItem(Book book, boolean empty) {
        super.updateItem(book, empty);

        if(empty || book == null) {
            setText(null);
            setGraphic(null);
        }

        else{

            //Loading file with the modified list elements
            if(mLoader == null) {
                mLoader = new FXMLLoader(getClass().getResource("/com/example/reading/newReadingList.fxml"));
                mLoader.setController(this); //Set controller here, rather than on scenebuilder

                try{
                    mLoader.load();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }

            //Labels will have the book and author name
            bookName.setText(String.valueOf(book.getBookName()));
            authorName.setText(String.valueOf(book.getAuthorName()));


            setText(null);
            setGraphic(anchorPane);
        }


    }


}

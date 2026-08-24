package com.example.reading.api;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.InputStream;
import java.net.URL;

//Reference: See 'DropdownBookCellMarkup' in this api package

//Class is used to modify appearance of the combo box
//Causes author name and book name to be displayed
public class DropdownBookCell extends ListCell<BookData> {


    @FXML
    private Label book, author;

    @FXML
    private HBox hbox;

    @FXML
    private FXMLLoader load;

    @FXML
    private ImageView cover;  //To show book cover



    @Override
    protected void updateItem(BookData bookData, boolean empty) {
        super.updateItem(bookData, empty);

        if(empty || bookData == null) {
            setText(null);
            setGraphic(null);
        }
        else {

            if (load == null) {
                load = new FXMLLoader(getClass().getResource("/com/example/reading/bookDropdown.fxml"));
                load.setController(this);

                try {
                    load.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            book.setText(String.valueOf(bookData.getBookName()));
            author.setText(String.valueOf(bookData.getAuthor()));

            if (bookData.getEditionPath() != null) {
                String[] path = bookData.getEditionPath().split("/");
                String imageCode = path[2];
            }

            setText(null);
            setGraphic(hbox);
        }


        }

    }



//THIS IS THE MARKED UP VERSION OF THE BOOKLISTVIEW CELL


//REFERENCE: Johannes (2016) 'Custom ListCell in a JavaFX ListView', Turais, 23 May.
// Available at: https://www.turais.de/how-to-custom-listview-cell-in-javafx/ (Accessed: 11 April 2026)


//Changes made shown below:


//REMOVED IMPORTS
/*
package de.turais.samples;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;

MY IMPORTS
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

 */
/*
 * Created by Johannes on 23.05.16.
 *
 *//*

ADDED NEW NAME - DROPDOWN BOOK CELL
public class StudentListViewCell extends ListCell<Student> {

    @FXML
    private Label label1;

    @FXML
    private Label label2;

REMOVED
    @FXML
    private FontAwesomeIconView fxIconGender;

REMOVED
    @FXML
    private GridPane gridPane;

    private FXMLLoader mLLoader;

ADDED - SHOW BOOK COVER
 @FXML
    private ImageView cover;

ADDED - USE HBOX INSTEAD OF GRID
 @FXML
    private HBox hbox;


REMOVED STUDENT CLASS - USE BOOKDATA INSTEAD
    @Override
    protected void updateItem(Student student, boolean empty) {
        super.updateItem(student, empty);

//REMOVED STUDENT REFERENCE
        if(empty || student == null) {

//ADDED
        if(empty || bookData == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
//REMOVED FILE PATH.
                mLLoader = new FXMLLoader(getClass().getResource("/fxml/ListCell.fxml"));

              //ADDED OWN FILE PATH
                load = new FXMLLoader(getClass().getResource("/com/example/reading/bookDropdown.fxml"));

                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

//REMOVED THIS SECTION BELOW
            label1.setText(String.valueOf(student.getStudentId()));
            label2.setText(student.getName());

            if(student.getGender().equals(Student.GENDER.MALE)) {
                fxIconGender.setIcon(FontAwesomeIcon.MARS);
            } else if(student.getGender().equals(Student.GENDER.FEMALE)) {
                fxIconGender.setIcon(FontAwesomeIcon.VENUS);
            } else {
                fxIconGender.setIcon(FontAwesomeIcon.GENDERLESS);
            }

//ADDED SECTION BELOW
            book.setText(String.valueOf(bookData.getBookName()));
            author.setText(String.valueOf(bookData.getAuthor()));

            if (bookData.getEditionPath() != null) {
                String[] path = bookData.getEditionPath().split("/");
                String imageCode = path[2];
               // getPhoto(imageCode);
            }


            setText(null);
 //REMOVED
            setGraphic(gridPane);

  //ADDED
            setGraphic(hbox);

        }

    }
}
*/

//Annotations for 'BookListViewCell' class

//REFERENCE:
// Johannes (2016) 'Custom ListCell in a JavaFX ListView', Turais, 23 May.
// Available at: https://www.turais.de/how-to-custom-listview-cell-in-javafx/ (Accessed: 11 April 2026).


/*

/**
 * Created by Johannes on 23.05.16.
 *
 */
/*

//REMOVED NAMES
public class StudentListViewCell extends ListCell<Student> {

/ADDED OWN
public class BookListViewCell extends ListCell<Book>

//REMOVED LABELS
    @FXML
    private Label label1;

    @FXML
    private Label label2;

//ADDED OWN ONES
    @FXML
    private Label bookName;

    @FXML
    private Label authorName;


//REMOVED
    @FXML
    private FontAwesomeIconView fxIconGender;

//REMOVED
    @FXML
    private GridPane gridPane;

//ADDED ANCHOR
    @FXML
    private AnchorPane anchorPane;

    private FXMLLoader mLLoader;

    @Override
    //REMOVED STUDENT
    protected void updateItem(Student student, boolean empty) {
    super.updateItem(student, empty);

    //ADDED BOOK PARAMETER
     protected void updateItem(Book book, boolean empty) {
        super.updateItem(book, empty);

        if(empty || student == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {

     //REMOVED FXML FILE
                mLLoader = new FXMLLoader(getClass().getResource("/fxml/ListCell.fxml"));

    //ADDED OWN FXML
                mLoader = new FXMLLoader(getClass().getResource("/com/example/reading/newReadingList.fxml"));

                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

//REMOVED SECTION
            label1.setText(String.valueOf(student.getStudentId()));
            label2.setText(student.getName());

            if(student.getGender().equals(Student.GENDER.MALE)) {
                fxIconGender.setIcon(FontAwesomeIcon.MARS);
            } else if(student.getGender().equals(Student.GENDER.FEMALE)) {
                fxIconGender.setIcon(FontAwesomeIcon.VENUS);
            } else {
                fxIconGender.setIcon(FontAwesomeIcon.GENDERLESS);
            }

//ADDED OWN GUI ELEMENTS
            bookName.setText(String.valueOf(book.getBookName()));
            authorName.setText(String.valueOf(book.getAuthorName()));



            setText(null);

//REMOVED
            setGraphic(gridPane);

//ADDED
            setGraphic(anchorPane);

        }

    }
}


 */

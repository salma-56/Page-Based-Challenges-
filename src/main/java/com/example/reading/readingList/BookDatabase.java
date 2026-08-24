package com.example.reading.readingList;

import com.example.reading.HomePageController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

//To retrieve,insert and delete books from the database
public class BookDatabase {


    //Create table to store user's books
    public void createTable(Connection conn) {
        PreparedStatement statement;

        try {
             statement = conn.prepareStatement
                     ("create table if not exists book_table (isbn SERIAL, book_name text, author_name text, total_pages integer, pages_read integer, cover_image text, user_id integer, primary key(isbn)," +
                             "constraint fk_user_table foreign key(user_id) references user_table(user_id));");

            statement.executeUpdate();
            System.out.println("Created book table");

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    //Check if book exists in database before inserting a new one
    //Return null if no book with same name exists
    //For match: Books need same title and author
    public String checkBook(Connection conn, String bookName, String author) {
        PreparedStatement statement;
        ResultSet rs;
        String book = null;

        try{
            statement=conn.prepareStatement("select * from book_table where book_name = ? and author_name = ? and user_id =?");
            statement.setString(1,bookName);
            statement.setString(2,author);
            statement.setInt(3, HomePageController.userId);

            rs = statement.executeQuery();

            while(rs.next()) {
                book = rs.getString(2);

          }

        }
        catch(Exception e) {
            e.printStackTrace();
        }


        return book;
    }




    //Connect to database

    /*
Namaste Coding (2021) JAVA + PostgreSQL CRUD in 2021 !PART 1: Connecting to PostgreSQL server. 18 January.
 Available at: https://www.youtube.com/watch?v=o9dcSS_82gw&list=PL0vVAYYSRbD2zL7o_TBPnVAgBZmg6f4JA
(Accessed: 11 April 2026)

     */
    public Connection connect(String dbName, String user, String password) {
        Connection conn=null;

        try{

            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbName,user,password);
            if(conn!=null) {
                System.out.println("Connected!");
            }
            else{
                System.out.println("Connection failed");
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }

        return conn;
    }


    //Insert book information into database:
    public void prepInsert(Connection conn, String bookName, String authorName) {

        PreparedStatement prepStatement = null;
        try {


            prepStatement = conn.prepareStatement("insert into book_table (book_name, author_name, total_pages, pages_read, cover_image, user_id) values (?,?,?,?,?,?)");

            prepStatement.setString(1, bookName);
            prepStatement.setString(2, authorName);
            prepStatement.setNull(3, Types.NULL);
            prepStatement.setNull(4, Types.NULL);
            prepStatement.setNull(5,Types.NULL);
            prepStatement.setInt(6, HomePageController.userId);
            prepStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void apiInsert(Connection conn, String bookName, String authorName, int totalPages, String coverUrl){
        PreparedStatement statement;

        try{
            statement=conn.prepareStatement("insert into book_table(book_name,author_name,total_pages,cover_image, user_id) values (?,?,?,?,?)");
            statement.setString(1, bookName);
            statement.setString(2, authorName);

            if(totalPages> -1) {
                statement.setInt(3, totalPages);
            }
            else{
                statement.setNull(3, Types.NULL);
            }
            statement.setString(4, coverUrl);
            statement.setInt(5,HomePageController.userId);

            statement.executeUpdate();

        }catch(Exception e) {
            e.printStackTrace();
        }

    }



    //Read book information stored in database
    public ObservableList<Book> readPrepData(Connection conn) {
        PreparedStatement prepStatement;
        ResultSet rs;
        ObservableList<Book> booksList = FXCollections.observableArrayList();

        try {
            prepStatement = conn.prepareStatement("Select * from book_table where user_id = ?");
           prepStatement.setInt(1, HomePageController.userId);
            rs = prepStatement.executeQuery();

            while(rs.next()) {

                int bookId = rs.getInt(1);
                String bookTitle = rs.getString(2);
                String authorName = rs.getString(3);

                Book book = new Book(bookId, bookTitle, authorName);

                int totalPages = rs.getInt(4);
                String coverUrl = rs.getString(6);
                if(totalPages > 0) {
                    book.setTotalPages(totalPages);
                }

                if(coverUrl!=null) {
                    book.setCover(coverUrl);
                }


                booksList.add(book);
            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return booksList;
    }

    //Delete book from database
    public void deletePrep(Connection conn, int bookId) {
        try {
            PreparedStatement statement = conn.prepareStatement("delete from book_table where isbn = ? and user_id =?");
            statement.setInt(1, bookId);
            statement.setInt(2, HomePageController.userId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();        }


    }

}

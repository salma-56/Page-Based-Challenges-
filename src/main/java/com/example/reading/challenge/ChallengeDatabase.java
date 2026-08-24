package com.example.reading.challenge;

import com.example.reading.HomePageController;
import com.example.reading.readingList.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javax.swing.plaf.nimbus.State;
import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class ChallengeDatabase {


    /*
Namaste Coding (2021) JAVA + PostgreSQL CRUD in 2021 !PART 1: Connecting to PostgreSQL server. 18 January.
Available at: https://www.youtube.com/watch?v=o9dcSS_82gw&list=PL0vVAYYSRbD2zL7o_TBPnVAgBZmg6f4JA
(Accessed: 11 April 2026)

Used for database connection function
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


    //Create table for challenges
    public void createTable(Connection conn) {
        Statement statement;
        String query = "create table if not exists challenge (challenge_id SERIAL, start_date Date, end_date Date, start_page integer, end_page integer, challenge_name text, status text, progress integer, book_name text, book_id integer, user_id integer, primary key(challenge_id)," +
                "constraint fk_user_table foreign key(user_id) references user_table(user_id), " +
                "constraint fk_book_table foreign key(book_id) references book_table(isbn) on delete set null);";

        /*
        Neon (no date) PostgreSQL Foreign Key Available at:https://neon.com/postgresql/postgresql-tutorial/postgresql-foreign-key (Accessed: 11 April 2026)
        Used for foreign key syntax deletion
         */
        try {
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Created new challenge table");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    //Go through the active challenges and see if any have failed
    //Count number of active challenges are now in the fail state
    //If result is 0, no newly failed challenges
    //If result > 0, there are  challenges today which need to be updated to fail
    //If -ve returned, then error counting rows
    public int countFailedChallenges(Connection conn) {
        PreparedStatement statement;
        ResultSet rs;
        int numOfFailedChallenge = -1;  //Set to -ve for error handling


        try{
            statement=conn.prepareStatement("select count(*) from challenge where progress<end_page and ? >end_date and status = ? and user_id = ?");
            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setString(2, "Ongoing"); //Without this extra condition - database will count all past challenges, not the ones in need of update
            statement.setInt(3, HomePageController.userId);
            rs = statement.executeQuery();

            while(rs.next()) {
                 numOfFailedChallenge = rs.getInt(1);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return numOfFailedChallenge;
    }


    //Changing status to 'fail' for the incomplete challenges
    //Can remove book_id - optional relationship between failed challenge and book
    public void setFailedChallenge(Connection conn) {
        PreparedStatement statement;
        try{
            statement=conn.prepareStatement("update challenge set status='Failed', book_id = null where progress < end_page and ? > end_date and user_id = ? ;");
            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setInt(2, HomePageController.userId);
            statement.executeUpdate();
            statement.close();
        }catch(Exception e){e.printStackTrace();}
    }

    //Completed a challenge - change status
    //Remove book_id to maintain record if book is removed
    public void setCompletedChallenge(Connection conn, int challengeId) {
        PreparedStatement statement;
        try{
            statement=conn.prepareStatement("update challenge set status='Complete', book_id = null where challenge_id = ? and user_id = ?;");
            statement.setInt(1,challengeId);
            statement.setInt(2, HomePageController.userId);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //ChallengeList calls this function - but its not really in use due to speed
    public String lookupBookName(Connection conn, int selectedBookId) {

        String bookName = null;
        PreparedStatement statement;
        ResultSet rs;

        try{
           statement=conn.prepareStatement("select book_name from book_table where isbn = ?");
           statement.setInt(1, selectedBookId);
           rs = statement.executeQuery();

           while(rs.next()) {
               System.out.println("read");
               bookName = rs.getString(1);
               System.out.println(bookName);
           }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return bookName;
    }




    //Inserting challenge
    public void insertChallenge(Connection conn, LocalDate startDate, LocalDate endDate, int startPg, int endPg, String challengeName, String bookName, int bookID) {

        PreparedStatement prepStatement = null;
        try {
            prepStatement = conn.prepareStatement("insert into challenge (start_date, end_date, start_page, end_page, challenge_name, status, progress, book_name, book_id, user_id) values (?,?,?,?,?,?,?,?,?,?)");
            prepStatement.setDate(1, Date.valueOf(startDate));
            prepStatement.setDate(2, Date.valueOf(endDate));
            prepStatement.setInt(3, startPg);
            prepStatement.setInt(4,endPg);
            prepStatement.setString(5, challengeName);
            prepStatement.setString(6, "Ongoing");
            prepStatement.setInt(7, startPg);  //Initially challenge would have no progress - so set to the first page of challenge
            prepStatement.setString(8, bookName);
            prepStatement.setInt(9, bookID);
            prepStatement.setInt(10, HomePageController.userId);

            System.out.println("Inserted Challenge");


            prepStatement.execute();

        } catch (Exception e) {
            System.out.println(e);

        }
    }


//Used to remove a challenge from a list
    public void removeChallenge(Connection conn, int challengeID) {
        PreparedStatement preparedStatement;

        try{
            preparedStatement = conn.prepareStatement("delete from challenge where challenge_id = ? and user_id =?");
            preparedStatement.setInt(1, challengeID);
            preparedStatement.setInt(2, HomePageController.userId);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Deleted challenge!");

        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    //Earliest date is read first
    //So challenge due in earlier show up first
    public ObservableList<Challenge> readChallengeDataOrdered(Connection conn, String status) {
        ObservableList<Challenge> challengeObservableList = FXCollections.observableArrayList();
        PreparedStatement preparedStatement;
        ResultSet rs;
        LocalDate today = LocalDate.now(); //Ensure only active challenges show up. Not overdue ones

        try{
          preparedStatement = conn.prepareStatement("select * from challenge where status = ? and user_id = ? order by end_date");


            preparedStatement.setString(1, status);  //Get any challenge type
            preparedStatement.setInt(2, HomePageController.userId);
            rs = preparedStatement.executeQuery();


            while(rs.next()) {
                int challengeId = rs.getInt(1);  //Retrieve auto-generated primary key
                LocalDate startDate = rs.getDate(2).toLocalDate();
                LocalDate endDate = rs.getDate(3).toLocalDate();
                int startPage = rs.getInt(4);
                int endPage = rs.getInt(5);
                String challengeName = rs.getString(6);
                String challengeStatus = rs.getString(7);
                int challengeProgress = rs.getInt(8);
                String bookSelected = rs.getString(9);
                int bookId = rs.getInt(10);


                 challengeObservableList.add(new Challenge(challengeId, startDate, endDate, startPage, endPage, challengeName, challengeStatus, challengeProgress, bookSelected, bookId));
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }


        return challengeObservableList;
    }



    //Read all challenges considered 'Failed' and 'Completed'
    //Show most recent end_dates first
    //Used to show record of all past challenges
    public ObservableList<Challenge> readPastChallenges(Connection conn){
        PreparedStatement statement;
        ResultSet rs;
        ObservableList<Challenge> pastChallenges = FXCollections.observableArrayList();
        try{
            statement = conn.prepareStatement("select * from challenge where status != 'Ongoing' and user_id = ? order by end_date desc;");
            statement.setInt(1, HomePageController.userId);
            rs=statement.executeQuery();

            //method() - give it rs. and observable list. let it add challenges
            while(rs.next()) {
                int challengeId = rs.getInt(1);  //Retrieve auto-generated primary key
                LocalDate startDate = rs.getDate(2).toLocalDate();
                LocalDate endDate = rs.getDate(3).toLocalDate();
                int startPage = rs.getInt(4);
                int endPage = rs.getInt(5);
                String challengeName = rs.getString(6);
                String challengeStatus = rs.getString(7);
                int challengeProgress = rs.getInt(8);
                String bookSelected = rs.getString(9);
                int bookId = rs.getInt(10);


                pastChallenges.add(new Challenge(challengeId, startDate, endDate, startPage, endPage, challengeName, challengeStatus, challengeProgress, bookSelected, bookId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  pastChallenges;
    }

    //Update the current page
    public void updateProgress(Connection conn, int newProgress, int challengeID){
        PreparedStatement preparedStatement;

        try{
           preparedStatement = conn.prepareStatement("update challenge set progress = ? where challenge_id = ? and user_id = ?");
           preparedStatement.setInt(1, newProgress);
           preparedStatement.setInt(2, challengeID);
           preparedStatement.setInt(3, HomePageController.userId);
           preparedStatement.executeUpdate();
           preparedStatement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }



    }



}

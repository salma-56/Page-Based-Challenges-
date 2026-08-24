package com.example.reading.user;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

//Validates user information e.g. their login details
//Also used for system updates - user stores a log/date for when their challenges were last updated.
public class UserDatabase {


    private Connection conn;
    private PreparedStatement statement;
    private LocalDate today;

    public UserDatabase(Connection conn) {
        this.conn=conn;
    }


    //For storing user data
    public void createUserTable(Connection conn) {
        try {
            statement = conn.prepareStatement("create table if not exists user_table(user_id serial, username text,password text, counter integer, counter_reset Timestamp, last_log Date,  primary key(user_id));");
            statement.executeUpdate();
            System.out.println("Create User Table");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Inserting new user into database
    public void insertUser(String username, String password) {

        try {
            statement = conn.prepareStatement("insert into user_table(username,password, counter) values (?,?,?);");
            statement.setString(1, username);
            statement.setString(2, password);

            //Used for randomise book function:
            statement.setInt(3, 2); //Giving user 2 counter's on set up

            //For new user - don't insert into timestamp or last_log. Leave as null.

            statement.executeUpdate();
            System.out.println("Inserted user!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Check if username is unique when making account
    public String checkUsername(String newUsername){
        PreparedStatement statement;
        ResultSet rs;
        String user = null;
        try{
            statement= conn.prepareStatement("select username from user_table where username = ?");
            statement.setString(1, newUsername);
            rs = statement.executeQuery();

            while(rs.next()) {
                user = rs.getString(1);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return user;
    }



    //Check the username and password for a user:
    public int checkPassword(String username, String password) {


        int userId = -1;
        ResultSet rs;
        try {
            statement = conn.prepareStatement("select user_id from user_table where username = ? and password = ?;");
            statement.setString(1, username);
            statement.setString(2,password);
             rs = statement.executeQuery();

             //If username and password are correct, then successful login. Return true
             while(rs.next()) {
                 userId = rs.getInt(1);

             }

            return userId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userId;

    }

    //Update total number of counters the user has
    public void updateCounter(int newCounter, int userId){
        try{
            statement=conn.prepareStatement("update user_table set counter = ? where user_id = ?;");
            statement.setInt(1, newCounter); //Give user 2 counter on reset
            statement.setInt(2, userId);
            statement.executeUpdate();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Retrieves counter the user has
    //Used to check if the user can randomise books
    public int retrieveCounter(int userId) {
        int counter = -1; //For error handling, cannot have -1 counter
        ResultSet rs;
        try {
            statement = conn.prepareStatement("select counter from user_table where user_id = ?");
            statement.setInt(1, userId);
            rs = statement.executeQuery();

            while (rs.next()) {
                counter = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return counter;
    }

    //Get time when counter will be reset
    //Used to display message to user for when randomiser is available
    public LocalDateTime retrieveCounterResetTime(int userId) {
        ResultSet rs;
        LocalDateTime resetTime = null;

        try{
            statement=conn.prepareStatement("select counter_reset from user_table where user_id = ?");
            statement.setInt(1,userId);
            rs = statement.executeQuery();

            while(rs.next()) {
                resetTime = rs.getTimestamp(1).toLocalDateTime();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return resetTime;
    }

    //Randomiser now unavailable.
    // Log time when counter can be used again
    public void updateCounterTime(LocalDateTime resetTime, int userId) {

        try {
            statement = conn.prepareStatement("update user_table set counter_reset = ? where user_id = ?");
            statement.setTimestamp(1, Timestamp.valueOf(resetTime));
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        }







    //Reading the date for when last log occurred
    //If its today - do nothing as it implies inactive challenges were found and set to failed already
    //If its in past - then inactive challenges are updated
    //If future - still do nothing....is this right? or should we still update just in case?

    //Returns date - the last time the challenges were updated
    //Or returns null if no checks happened yet (eg new account)

    public LocalDate readLastLog(Connection conn, int userId) {
        ResultSet rs;
        LocalDate lastLogDate = null;

        try {
            statement = conn.prepareStatement("select last_log from user_table where user_id = ?");
            statement.setInt(1,userId);
            rs = statement.executeQuery();

            //Retrieve the last update date
            while(rs.next()) {
                Date recentLog = rs.getDate(1);

                //If there is a record, convert to LocalDate and return date of last system check
                if(recentLog != null) {
                    lastLogDate = recentLog.toLocalDate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lastLogDate;
    }


    //Used to update log date to today
    //So no more searches for inactive challenges for rest of today
    public void updateLogRecord(Connection conn, int userId) {
        today = LocalDate.now();
        try {
            statement = conn.prepareStatement("update user_table set last_log=? where user_id = ?; ");
            statement.setDate(1, Date.valueOf(today));
            statement.setInt(2, userId);
            statement.executeUpdate();
            System.out.println("Updated log record.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}

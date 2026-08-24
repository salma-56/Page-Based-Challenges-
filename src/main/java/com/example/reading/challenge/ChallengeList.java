package com.example.reading.challenge;

import javafx.collections.ObservableList;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;


//A collection class which stores and manipulates challenges
public class ChallengeList {

    //Contains all challenges from db. Updates during read - not in use.
    static ArrayList<Challenge> challenges;
    private Connection conn;
    private ChallengeDatabase challengeDb;


    public ChallengeList() {
        challengeDb = new ChallengeDatabase();
        conn = challengeDb.connect("bookapp","postgres","oracle");
    }


    //Make connections in each method or constructor?

    //Requires start_date, end_date, start_page, end_page, book_id
    public void addChallenge(LocalDate startDate, LocalDate endDate, int startPage, int endPage, String challengeName, String bookName, int bookID) {
      challengeDb.insertChallenge(conn, startDate, endDate, startPage, endPage, challengeName, bookName, bookID);
    }//No need to pass progress reference,new challenge so progress is 0.


    //Remove challenge from database
    public void removeChallenge(int challengeID) {
        challengeDb.removeChallenge(conn, challengeID);
    }



    //Retrieves earliest challenges first
    public ObservableList<Challenge> retrieveEarliestChallenges(String status){return challengeDb.readChallengeDataOrdered(conn, status);}


    //Retrieve failed challenges - removed
    public ObservableList<Challenge> retrievePastChallenge() {return challengeDb.readPastChallenges(conn);}


    //Update the number of pages the user has read
    public void updatePage(int newProgress, int challengeId) {
        challengeDb.updateProgress(conn, newProgress, challengeId);
    }

    //Used to set challenge status
    public void completeChallenge(int challengeId) {
        challengeDb.setCompletedChallenge(conn, challengeId);
    }



    //To retrieve book name for associated challenge
    //Used to make challenge description
    public String lookupBookName(int challengeId) {
        return challengeDb.lookupBookName(conn, challengeId);
    }
}

package com.example.reading.challenge;

import com.example.reading.readingList.Book;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;

public class Challenge {

    //Properties of a challenge
    private int challengeID;
    private String challengeBook;
    private LocalDate startDate;
    private LocalDate endDate;
    private int startPage;
    private int endPage;
    private int bookID;
    private String challengeDesc;  //Contains above fields. Not the below

    private String challengeStatus;
    private String challengeName;
    private int challengeProgress;

    private int daysRemaining;


    public Challenge(int challengeId, LocalDate startDate, LocalDate endDate, int startPage, int endPage, String challengeName, String challengeStatus, int challengeProgress, String challengeBook, int book_id) {
        this.challengeID = challengeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startPage = startPage;
        this.endPage = endPage;
        this.bookID = book_id;  //Have to handle later -> convert book ID to the actual book
        this.challengeName = challengeName;
        this.challengeStatus = challengeStatus;
        this.challengeProgress = challengeProgress;
        this.challengeBook = challengeBook;
    }


    //Calculate number of days left for a challenge
    //Use to show user the time remaining
    public int getDaysRemaining() {


        /*
        Obregon,A. (2025) Java's Period.between() Method Explained.
        Available at: https://medium.com/@AlexanderObregon/javas-period-between-method-explained-c32f4cd996c6 (Accessed: 11 April 2026).
         */
        return Period.between(startDate,endDate).getDays(); //Use updated Time instead. For this one, start date comes first or else -ve

        //Need the later date first or else get negatives
        //E.g End date = 5th  Today =10th   5-10=-5 -> overdue by 5 days
    }


    //Excludes status and progress
    public String getChallengeDesc() {
        String c_id = String.valueOf(challengeID);
        String s_page = String.valueOf(startPage);
        String e_page = String.valueOf(endPage);


       return c_id + " " + challengeName + " " + startDate.toString() + " " +endDate.toString() + " " + s_page + " "+ e_page + " " + startPage + " " + endPage + " " + bookID;

    }


    //Calculate the % of the challenge the user has completed
    //Show the % in the dashboard view

    //Need a condition for update- current page must be beyond start page
    public double calculatePercentage(int startPage, int endPage, int currentPage) {

        int totalPages = endPage - startPage;
        int pagesRead = currentPage - startPage;

        return (double) pagesRead /totalPages;
    }


    //Getters and setters
    public int getChallengeID() {
        return challengeID;
    }

    public int getChallengeProgress() {
        return challengeProgress;
    }



    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public int getBookID() {
        return bookID;
    }

    public String getChallengeStatus() {
        return challengeStatus;
    }

    public String getChallengeName() {
        return challengeName;
    }

    //Used so user can see their chosen book in the challenges list view
    public String getChallengeBook() {
        return challengeBook;
    }
}

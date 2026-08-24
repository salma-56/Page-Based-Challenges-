package com.example.reading.api;

import java.util.ArrayList;

//Encapsulates data received from both API calls
public class BookData {

    //Information on work (collection of books)
    private String bookName;
    private String author;
    private String editionPath;

    //Information on edition
    //information includes page numbers and book cover url
    private int totalPages = -1;
    private String coverUrl;

    static ArrayList<BookData> bookResults = new ArrayList<>();

    //Retrieves information on works
    //Includes book name, author, and url to the editions
    public BookData(String bookName, String authorName, String editionPath) {
        this.bookName = bookName;
        this.author = authorName;
        this.editionPath = editionPath;
        bookResults.add(this); //Add to list
    }

    public BookData() {
    }

    //Set book information retrieved from api
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setAuthorName(String author) {
        this.author = author;
    }

    public void setTotalPages(int pages) {
        this.totalPages = pages;
    }

    public void setCoverUrl(String url) {
        this.coverUrl = url;
    }


    //Use to display results to GUI
    public String getBookName() {return bookName;}

    public String getAuthor() {
        return author;
    }

    public String getEditionPath() {
        return editionPath;
    }

    public String getCoverUrl() {return coverUrl;}

    public int getTotalPages() {return totalPages;}

}

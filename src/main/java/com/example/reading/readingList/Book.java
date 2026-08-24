package com.example.reading.readingList;

public class Book {

   private int bookId;
   private String bookName;
   private String authorName;
   private String bookDesc;

   //Stores url path for cover image
   private String cover;

   //Total pages can be found using API
   //Include -ve for error handling
   private int totalPages = -1;

   //Entity class to represent user's books
    public Book(int book_id, String book_name, String author_name) {
        bookId = book_id;
        bookName = book_name;
        authorName = author_name;


        //Create a book description
        this.bookDesc = book_name + " " +  author_name;

    }


    //Getters and setters:
    public String getbookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    //Need for deleting books:
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    //Need for displaying book information
    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}

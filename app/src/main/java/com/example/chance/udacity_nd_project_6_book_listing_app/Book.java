package com.example.chance.udacity_nd_project_6_book_listing_app;

/**
 * Created by chance on 8/6/17.
 */


public class Book {
    private String mBookTitle;
    private String mBookAuthor;
    private int mBookImage;


    public Book(String mBookTitle, String mBookAuthor) {
        this.mBookTitle = mBookTitle;
        this.mBookAuthor = mBookAuthor;
    }

    public Book(String mBookTitle, String mBookAuthor, int mBookImage) {
        this.mBookTitle = mBookTitle;
        this.mBookAuthor = mBookAuthor;
        this.mBookImage = mBookImage;
    }

    public String getmBookTitle() {
        return mBookTitle;
    }

    public void setmBookTitle(String mBookTitle) {
        this.mBookTitle = mBookTitle;
    }

    public String getmBookAuthor() {
        return mBookAuthor;
    }

    public void setmBookAuthor(String mBookAuthor) {
        this.mBookAuthor = mBookAuthor;
    }

}

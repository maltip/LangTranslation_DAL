package com.example.langtranslation;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Holds the language code (i.e. "en") and the corresponding localized full language name
 *(i.e. "English")
 */

public class Language {
    private String code; //instance variable


    //Exercise 3a: Class constructor that accepts a string as the code.

    public Language(String code) {
        this.code = code;
    }

    String getDisplayName() {
        return new Locale(code).getDisplayName();
    }

    //Exercise 3b: getter function for the code.


    //Method to retrieve the Code.
    public String getCode() {
        return this.code;
    }

    //Method to set the Code.

    public void setCode(String code) {
        this.code = code;
    }

    @NonNull
    public String toString() {
        return code + " - " + getDisplayName();
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}

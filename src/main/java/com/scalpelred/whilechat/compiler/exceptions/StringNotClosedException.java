package com.scalpelred.whilechat.compiler.exceptions;

public class StringNotClosedException extends Exception {

    public final String Line;

    public StringNotClosedException(String line){
        Line = line;
    }
}

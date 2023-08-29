package com.scalpelred.whilechat.compiler.exceptions;

public class UnexpectedTokenException extends Exception {

    public final String Line;
    public final String Token;

    public UnexpectedTokenException(String line, String token){

        Line = line;
        Token = token;
    }
}


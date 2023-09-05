package com.scalpelred.whilechat.compiler.exceptions;

public class CantResolveTokenException extends Exception {

    public final String Token;

    public CantResolveTokenException(String token){
        Token = token;
    }

}

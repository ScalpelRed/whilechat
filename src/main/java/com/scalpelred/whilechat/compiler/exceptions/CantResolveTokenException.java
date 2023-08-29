package com.scalpelred.whilechat.compiler.exceptions;

public class CantResolveTokenException extends Exception {

    public final String Expression;
    public final String Token;

    public CantResolveTokenException(String expression, String value){
        Expression = expression;
        Token = value;
    }

}

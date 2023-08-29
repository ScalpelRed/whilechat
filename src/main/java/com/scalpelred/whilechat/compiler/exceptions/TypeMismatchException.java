package com.scalpelred.whilechat.compiler.exceptions;

import com.scalpelred.whilechat.compiler.Parser;

public class TypeMismatchException extends Exception {
    public final String Expression;
    public final Parser.Type ExpectedType;
    public final Parser.Type GotType;

    public TypeMismatchException(String expression, Parser.Type expectedType, Parser.Type gotType){
        Expression = expression;
        ExpectedType = expectedType;
        GotType = gotType;
    }
}

package com.scalpelred.whilechat.compiler.exceptions;

public class NoSuchOperatorException extends Exception {
    public final String Pattern;
    public final boolean Binary;
    public final boolean Prefix;


    public NoSuchOperatorException(String pattern, boolean prefix) {
        Pattern = pattern;
        Binary = false;
        Prefix = prefix;
    }

    public NoSuchOperatorException(String pattern) {
        Pattern = pattern;
        Binary = true;
        Prefix = false;
    }
}

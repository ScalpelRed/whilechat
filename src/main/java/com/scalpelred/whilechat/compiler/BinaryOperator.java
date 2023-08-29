package com.scalpelred.whilechat.compiler;

import com.scalpelred.whilechat.Program;
import com.scalpelred.whilechat.commands.Command;
import org.apache.commons.lang3.function.TriFunction;

public class BinaryOperator {

    public final Parser.Type LeftArg;
    public final Parser.Type RightArg;
    public final Parser.Type Result;
    public final String Pattern;
    public final int Priority;

    private final TriFunction<Integer, Integer, Integer, Command> InstFunc; // TODO rename

    public BinaryOperator(String pattern, Parser.Type leftArg, Parser.Type rightArg, Parser.Type result, int priority,
                          TriFunction<Integer, Integer, Integer, Command> instFunc) {
        LeftArg = leftArg;
        RightArg = rightArg;
        Result = result;
        Pattern = pattern;
        InstFunc = instFunc;
        Priority = priority;
    }

    public Command getCommand(int arg1, int arg2, int out){
        return InstFunc.apply(arg1, arg2, out);
    }
}

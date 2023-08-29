package com.scalpelred.whilechat.compiler;

import com.scalpelred.whilechat.commands.Command;

import java.util.function.BiFunction;

public class UnaryOperator {

    public final Parser.Type ArgType;
    public final Parser.Type OutType;
    public final String Pattern;

    private final BiFunction<Integer, Integer, Command> InstFunc; // TODO rename

    public UnaryOperator(String pattern, Parser.Type argType, Parser.Type outType, BiFunction<Integer, Integer, Command> instFunc) {
        ArgType = argType;
        OutType = outType;
        Pattern = pattern;
        InstFunc = instFunc;
    }

    public Command getCommand(int arg, int out){
        return InstFunc.apply(arg, out);
    }
}

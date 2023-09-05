package com.scalpelred.whilechat.compiler;

import com.scalpelred.whilechat.commands.Command;

import java.util.function.BiFunction;

public class UnaryOperator {

    public final Parser.Type ArgType;
    public final Parser.Type OutType;

    private final BiFunction<Integer, Integer, Command> InstFunc; // TODO rename

    public UnaryOperator(Parser.Type argType, Parser.Type outType, BiFunction<Integer, Integer, Command> instFunc) {
        ArgType = argType;
        OutType = outType;
        InstFunc = instFunc;
    }

    public Command getCommand(int arg, int out){
        return InstFunc.apply(arg, out);
    }
}

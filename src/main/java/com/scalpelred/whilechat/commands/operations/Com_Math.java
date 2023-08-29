package com.scalpelred.whilechat.commands.operations;

import com.scalpelred.whilechat.Program;
import com.scalpelred.whilechat.commands.Command;

public class Com_Math extends Command {

    private final int Arg1;
    private final int Arg2;
    private final int Out;
    private final Operation Operation;

    private final boolean NeedsArg1;
    private final boolean NeedsArg2;

    public Com_Math(Operation operation, int arg1, int arg2, int out) {
        Arg1 = arg1;
        Arg2 = arg2;
        Out = out;
        Operation = operation;

        NeedsArg1 = switch (operation){
            case PI, E -> false;
            default -> true;
        };

        NeedsArg2 = switch (operation){
            case NEGATE, INVERT -> false;
            default -> true;
        };
    }

    private final float PI = (float)Math.PI;
    private final float E = (float)Math.E;

    @Override
    public void Run(Program program) {

        float arg1 = NeedsArg1 ? program.getFloat(Arg1) : 0;
        float arg2 = NeedsArg2 ? program.getFloat(Arg2) : 0;

        float out = switch (Operation){
            case SUM -> arg1 + arg2;
            case SUBTRACT -> arg1 - arg2;
            case MULTIPLY -> arg1 * arg2;

            case NEGATE -> -arg1;
            case INVERT -> 1f / arg1;

            case PI -> PI;
            case E -> E;
        };

        program.setFloat(Out, out);
    }

    public enum Operation {
        SUM,
        SUBTRACT,
        MULTIPLY,

        NEGATE,
        INVERT,

        PI,
        E,

    }

    @Override
    public String toString(){
        return "MATH " + Arg1 + " " + Operation + " " + Arg2 + " TO " + Out;
    }
}

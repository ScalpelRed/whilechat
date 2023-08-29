package com.scalpelred.whilechat.commands.variables;

import com.scalpelred.whilechat.Program;
import com.scalpelred.whilechat.commands.Command;

public class Com_SetFloat extends Command {
    public final int Variable;
    public final float Float;

    public Com_SetFloat(int variable, float value) {
        Variable = variable;
        Float = value;
    }

    @Override
    public void Run(Program program) {
        program.setFloat(Variable, Float);
    }

    @Override
    public String toString(){
        return "SET " + Variable + " AS " + Float;
    }
}

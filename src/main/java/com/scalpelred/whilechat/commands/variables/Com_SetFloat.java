package com.scalpelred.whilechat.commands.variables;

import com.scalpelred.whilechat.Program;
import com.scalpelred.whilechat.commands.Command;

public class Com_SetFloat extends Command {
    public final int Variable;
    public final float Value;

    public Com_SetFloat(int variable, float value) {
        Variable = variable;
        Value = value;
    }

    @Override
    public void run(Program program) {
        program.setFloat(Variable, Value);
    }

    @Override
    public String toString(){
        return "SET " + Variable + " AS " + Value;
    }
}

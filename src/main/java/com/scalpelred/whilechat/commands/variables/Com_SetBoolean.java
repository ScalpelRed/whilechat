package com.scalpelred.whilechat.commands.variables;

import com.scalpelred.whilechat.Program;
import com.scalpelred.whilechat.commands.Command;

public class Com_SetBoolean extends Command {

    public final int Variable;
    public final boolean Value;

    public Com_SetBoolean(int variable, boolean value) {
        Variable = variable;
        Value = value;
    }

    @Override
    public void run(Program program) {
        program.setBoolean(Variable, Value);
    }

    @Override
    public String toString(){
        return "SET " + Variable + " AS " + Value;
    }
}

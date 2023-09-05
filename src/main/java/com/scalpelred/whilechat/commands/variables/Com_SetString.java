package com.scalpelred.whilechat.commands.variables;

import com.scalpelred.whilechat.Program;
import com.scalpelred.whilechat.commands.Command;

public class Com_SetString extends Command {

    public final int Variable;
    public final String Value;

    public Com_SetString(int variable, String value) {
        Variable = variable;
        Value = value;
    }

    @Override
    public void run(Program program) {
        program.setString(Variable, Value);
    }

    @Override
    public String toString(){
        return "SET " + Variable + " AS " + Value;
    }
}

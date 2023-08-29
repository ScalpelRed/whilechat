package com.scalpelred.whilechat.commands.variables;

import com.scalpelred.whilechat.Program;
import com.scalpelred.whilechat.commands.Command;

public class Com_SetString extends Command {

    public final int Variable;
    public final String String;

    public Com_SetString(int variable, String string) {
        Variable = variable;
        String = string;
    }

    @Override
    public void Run(Program program) {
        program.setString(Variable, String);
    }

    @Override
    public String toString(){
        return "SET " + Variable + " AS " + String;
    }
}

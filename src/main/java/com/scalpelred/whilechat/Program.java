package com.scalpelred.whilechat;

import java.util.HashMap;
import java.util.Stack;

import com.scalpelred.whilechat.commands.Command;

public final class Program {

    private final WhileChat WhileChat;

    private Command[] Commands;

    private int ExecutionLine;

    private final HashMap<Integer, String> Strings;
    private final HashMap<Integer, Float> Floats;
    private final HashMap<Integer, Boolean> Booleans;

    public Program(WhileChat whileChat) {
        WhileChat = whileChat;

        Strings = new HashMap<>();
        Floats = new HashMap<>();
        Booleans = new HashMap<>();

        Floats.put(0, 1f);
    }

    public void SetCommands(Command[] commands){
        Commands = commands;
    }

    public void run(String message, boolean isClientMessage) {
        Strings.put(0, message);
        Booleans.put(0, isClientMessage);

        for (ExecutionLine = 0; ExecutionLine < Commands.length; ExecutionLine++) {
            Command command = Commands[ExecutionLine];

            /*if (command.startsBlock()) CurrentBlock++;
            else if (command.endsBlock()) CurrentBlock--;*/

            // In skip mode commands will not execute
            // But if the command stops the block where skip mode was activated,
            //      it should execute to deactivate it.
            //if (!SkipMode || (command.endsBlock() && (ActiveBlocks.size() - 1 == CurrentBlock)))
                //command.run();
        }
    }


    // Active blocks are the blocks that were started out of skip mode.
    // All other blocks should be counted too, but they're not active.

    private boolean SkipMode = false;
    private final Stack<Integer> ActiveBlocks = new Stack<>();
    private int CurrentBlock;

    public void setSkipMode(boolean skipMode){
        SkipMode = skipMode;
    }

    public boolean isSkipMode(){
        return SkipMode;
    }

    public void addActiveBlock() {
        ActiveBlocks.add(ExecutionLine);
    }

    public void removeActiveBlock() {
        ActiveBlocks.pop();
    }

    public void goToLastBlock(){
        ExecutionLine = ActiveBlocks.peek() - 1;
    }

    public void stop() {
        ExecutionLine = Integer.MAX_VALUE;
    }


    public void setString(Integer index, String value) {
        Strings.put(index, value);
    }

    public void setFloat(Integer index, Float value) {
        Floats.put(index, value);
    }

    public void setBoolean(Integer index, Boolean value) {
        Booleans.put(index, value);
    }

    public String getString(Integer index) {
        return Strings.get(index);
    }

    public Float getFloat(Integer index) {
        return Floats.get(index);
    }

    public Boolean getBoolean(Integer index) {
        return Booleans.get(index);
    }

    public boolean stringExists(Integer index) { return Strings.containsKey(index); }

    public boolean floatExists(Integer index) { return Floats.containsKey(index); }

    public boolean booleanExists(Integer index) { return Booleans.containsKey(index); }


    public WhileChat getWhileChat() {
        return WhileChat;
    }


    public enum Type {
        STRING,
        NUMBER,
        BOOL,
    }
}
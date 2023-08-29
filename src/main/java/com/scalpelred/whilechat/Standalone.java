package com.scalpelred.whilechat;

import com.scalpelred.whilechat.compiler.Parser;
import com.scalpelred.whilechat.compiler.exceptions.*;

public class Standalone {

    public static void main(String[] args){
        new Standalone();
    }

    public Standalone(){
        try {
            new Parser().split("1 ++-2 * 3+4 *\"hu(e\\\"nwq\" + (5* 9) - a[10] + true + (\" aa\" * 9 + \"rtr\" + (\"asfdew\" * 8))");
        }
        /*catch (StringNotClosedException e) {
            System.out.println("String not closed: " + e.Line);
        }
        catch (UnexpectedTokenException e) {
            System.out.println("Unexpected token: " + e.Token);
        }
        catch (NoSuchOperatorException e) {
            System.out.println("Unknown operator: " + e.Pattern + " " + (e.Binary ? "binary" : "unary " + (e.Prefix ? "prefix" : "postfix")));
        }*/
        catch (CantResolveTokenException e) {
            System.out.println("Can't parse value: " + e.Token);
        }
        /*catch (TypeMismatchException e) {
            System.out.println("Type mismatch: expected " + e.ExpectedType + ", got " + e.GotType);
        }*/

    }
}

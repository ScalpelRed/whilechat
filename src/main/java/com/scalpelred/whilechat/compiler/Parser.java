package com.scalpelred.whilechat.compiler;

import com.scalpelred.whilechat.commands.Command;
import com.scalpelred.whilechat.commands.operations.Com_Math;
import com.scalpelred.whilechat.compiler.exceptions.*;

import java.util.*;
import java.util.regex.Pattern;

public final class Parser {

    private final static String StringRegex = "\\\"(?:[^\\\"]|(?<=\\\\)\\\")*\\\"";
    private final static String OperatorRegex = "[^\\w\\s]+";
    private final static String NumberRegex = "\\d+(?:\\.\\d+)?";
    private final static String NameRegex = "\\w+";

    private final static Pattern StringPattern = Pattern.compile(StringRegex);
    private final static Pattern OperatorPattern = Pattern.compile(OperatorRegex);
    private final static Pattern NumberPattern = Pattern.compile(NumberRegex);
    private final static Pattern NamePattern = Pattern.compile(NameRegex);

    private final static ArrayList<UnaryOperator> PrefixUnaryOperators = new ArrayList<>();
    private final static ArrayList<BinaryOperator> BinaryOperators = new ArrayList<>();

    static {
        PrefixUnaryOperators.add(new UnaryOperator("-", Type.NUMBER, Type.NUMBER,
                (Integer arg, Integer out) -> new Com_Math(Com_Math.Operation.NEGATE, arg, 0, out)));

        BinaryOperators.add(new BinaryOperator("+", Type.NUMBER, Type.NUMBER, Type.NUMBER, 0,
                (Integer arg1, Integer arg2, Integer out) -> new Com_Math(Com_Math.Operation.SUM, arg1, arg2, out)));
        BinaryOperators.add(new BinaryOperator("*", Type.NUMBER, Type.NUMBER, Type.NUMBER, 10,
                (Integer arg1, Integer arg2, Integer out) -> new Com_Math(Com_Math.Operation.MULTIPLY, arg1, arg2, out)));
    }

    public Parser() {

    }

    // разбирает строку на токены
    public ArrayList<Token> split(String expression) throws CantResolveTokenException {

        ArrayList<Token> tokens = new ArrayList<>();

        // текущий собираемый токен
        StringBuilder currentToken = new StringBuilder();
        CharGroupMode currentGroup = CharGroupMode.CONTINUE;

        Stack<Character> blocks = new Stack<>();

        for (int index = 0; index < expression.length(); index++) {

            char currentChar = expression.charAt(index);

            if (blocks.size() == 0 || blocks.peek() != '\"') {
                // если мы не в строке

                if (startsBlock(currentChar)) {
                    // Символ начинает блок

                    // мы в корне, и предыдущий токен нужно сохранить
                    if (blocks.size() == 0) {
                        String tokenStr = currentToken.toString();
                        tokens.add(new Token(tokenStr, getTokenType(expression, tokenStr)));
                        currentToken = new StringBuilder();
                    }
                    currentGroup = CharGroupMode.CONTINUE;
                    blocks.add(currentChar);
                    currentToken.append(currentChar);
                }
                else if (endsBlock(currentChar)) {
                    // символ завершает блок

                    blocks.pop();
                    currentToken.append(currentChar);

                    // блок в корне и является токеном
                    if (blocks.size() == 0) {
                        String tokenStr = currentToken.toString();
                        tokens.add(new Token(tokenStr, getTokenType(expression, tokenStr)));
                        currentToken = new StringBuilder();
                        currentGroup = CharGroupMode.CONTINUE;
                    }
                }
                else if (blocks.size() != 0) {
                    // пока мы в блоке, добавляем вообще всё
                    currentToken.append(currentChar);
                }
                else {
                    if (currentChar == ' '){
                        // пробел - конец токена
                        String tokenStr = currentToken.toString();
                        tokens.add(new Token(tokenStr, getTokenType(expression, tokenStr)));
                        currentToken = new StringBuilder();
                        currentGroup = CharGroupMode.CONTINUE;
                    }
                    else {
                        CharGroupMode newGroup = getCharGroup(currentChar);
                        if (newGroup != currentGroup){
                            // после имени встретился оператор (или наоборот)
                            if (currentGroup != CharGroupMode.CONTINUE) {
                                String tokenStr = currentToken.toString();
                                tokens.add(new Token(tokenStr, getTokenType(expression, tokenStr)));
                                currentToken = new StringBuilder();
                            }
                            currentGroup = newGroup;
                        }
                        currentToken.append(currentChar);
                    }
                }
            }
            else {
                // если в строке
                // строку нужно обрабатывать отдельно, так как в ней могут быть любые символы, но их не нужно учитывать

                currentToken.append(currentChar);

                if (currentChar == '\"' && expression.charAt(index - 1) != '\\') {
                    blocks.pop();

                    if (blocks.size() == 0){
                        // строка в корне и является токеном
                        tokens.add(new Token(currentToken.toString(), TokenType.VALUE_STRING));
                        currentToken = new StringBuilder();
                        currentGroup = CharGroupMode.CONTINUE;
                    }
                }
            }
        }
        // сохраняем последний токен, который в конце строки
        tokens.add(new Token(currentToken.toString(), getTokenType(expression, currentToken.toString())));
//
        // убираем пустые токены
        tokens.removeIf(token -> token.Type == TokenType.EMPTY);

        return tokens;
    }

    public enum Type {
        VOID,

        NUMBER,
        STRING,
        BOOLEAN,

        FUNCTION,
        ARRAY,
        DICTIONARY,
        REGEX,

    }

    public enum TokenType { // почему определять именно в таком порядке
        VALUE_STRING, // кавычки могут быть расценены как операторы
        VALUE_NUMBER, // числа могут быть расценены как часть имени
        VALUE_BOOLEAN, // могут быть расценены как имя
        EXPRESSION, // скобки могут быть расценены как операторы
        ARRAY_ACCESSOR, // скобки могут быть расценены как операторы
        CODE_BLOCK, // скобки могут быть расценены как операторы
        GENERIC, // скобки могут быть расценены как операторы TODO поменять название
        VARIABLE,
        OPERATOR_BUNCH, // непрерывный набор операторов, которые ещё нужно разделить

        BINARY_OPERATOR,
        PREFIX_UNARY_OPERATOR,
        POSTFIX_UNARY_OPERATOR,

        UNKNOWN,
        EMPTY,
    }

    private enum CharGroupMode {
        NAME, // в токене присутствуют цифры, буквы и нижнее подчеркивание
        OPERATOR, // противоположен NAME
        FINISHED, // начать новый в любом случае
        CONTINUE // продолжить этот в любом случае
    }

    public static boolean isBoolean(String token) {
        token = token.toLowerCase(Locale.ROOT);
        return token.equals("true") || token.equals("false");
    }

    public static TokenType getTokenType(String expression, String token) throws CantResolveTokenException {

        if (token.isEmpty()) return TokenType.EMPTY;

        // строка определяется и без этой функции
        if (NumberPattern.matcher(token).matches()) return TokenType.VALUE_NUMBER;
        if (isBoolean(token)) return TokenType.VALUE_BOOLEAN;

        if (token.startsWith("(") && token.endsWith(")")) return TokenType.EXPRESSION;
        if (token.startsWith("[") && token.endsWith("]")) return TokenType.ARRAY_ACCESSOR;
        if (token.startsWith("{") && token.endsWith("}")) return TokenType.CODE_BLOCK;
        if (token.startsWith("<") && token.endsWith(">")) return TokenType.GENERIC;
        if (NamePattern.matcher(token).matches()) return TokenType.VARIABLE;
        if (OperatorPattern.matcher(token).matches()) return TokenType.OPERATOR_BUNCH;

        return TokenType.UNKNOWN;


        //throw new CantResolveTokenException(expression, token);
    }

    private static CharGroupMode getCharGroup(char Char) {
        if (Char == '_' || Character.isLetterOrDigit(Char)) return CharGroupMode.NAME;
        else return CharGroupMode.OPERATOR;
        // TODO здесь должны отдельным if-ом определяться символы наподобие {, }, [, ] и т.д.
    }

    private static boolean startsBlock(char Char) {
        return Char == '\"' ||
                Char == '(' ||
                Char == '[' ||
                Char == '{' ||
                Char == '<';
    }

    private static boolean endsBlock(char Char) {
        return Char == ')' ||
                Char == ']' ||
                Char == '}' ||
                Char == '>';
    }
}

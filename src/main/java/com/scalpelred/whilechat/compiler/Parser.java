package com.scalpelred.whilechat.compiler;

import com.scalpelred.whilechat.commands.Command;
import com.scalpelred.whilechat.commands.operations.Com_Math;
import com.scalpelred.whilechat.commands.other.Com_Empty;
import com.scalpelred.whilechat.compiler.exceptions.*;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.world.level.biome.BiomeManager;
import org.lwjgl.system.CallbackI;

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

    private final HashMap<String, UnaryOperator> PrefixUnaryOperators = new HashMap<>();
    private final HashMap<String, UnaryOperator> PostfixUnaryOperators = new HashMap<>();
    private final HashMap<String, BinaryOperator> BinaryOperators = new HashMap<>();

    public Parser() {
        PrefixUnaryOperators.put("+", new UnaryOperator(Type.NUMBER, Type.NUMBER,
                (Integer arg, Integer out) -> new Com_Empty()));
        PrefixUnaryOperators.put("-", new UnaryOperator(Type.NUMBER, Type.NUMBER,
                (Integer arg, Integer out) -> new Com_Empty()));
        PrefixUnaryOperators.put("++", new UnaryOperator(Type.NUMBER, Type.NUMBER,
                (Integer arg, Integer out) -> new Com_Empty()));
        PrefixUnaryOperators.put("--", new UnaryOperator(Type.NUMBER, Type.NUMBER,
                (Integer arg, Integer out) -> new Com_Empty()));

        PostfixUnaryOperators.put("++", new UnaryOperator(Type.NUMBER, Type.NUMBER,
                (Integer arg, Integer out) -> new Com_Empty()));
        PostfixUnaryOperators.put("--", new UnaryOperator(Type.NUMBER, Type.NUMBER,
                (Integer arg, Integer out) -> new Com_Empty()));

        BinaryOperators.put("+", new BinaryOperator(Type.NUMBER, Type.NUMBER, Type.NUMBER, 0,
                (Integer arg1, Integer arg2, Integer out) -> new Com_Math(Com_Math.Operation.SUM, arg1, arg2, out)));
        BinaryOperators.put("-", new BinaryOperator(Type.NUMBER, Type.NUMBER, Type.NUMBER, 0,
                (Integer arg1, Integer arg2, Integer out) -> new Com_Math(Com_Math.Operation.SUBTRACT, arg1, arg2, out)));
        BinaryOperators.put("*", new BinaryOperator(Type.NUMBER, Type.NUMBER, Type.NUMBER, 100,
                (Integer arg1, Integer arg2, Integer out) -> new Com_Math(Com_Math.Operation.MULTIPLY, arg1, arg2, out)));
        BinaryOperators.put("/", new BinaryOperator(Type.NUMBER, Type.NUMBER, Type.NUMBER, 100,
                (Integer arg1, Integer arg2, Integer out) -> new Com_Math(Com_Math.Operation.DIVIDE, arg1, arg2, out)));
    }

    // разбирает строку на токены
    public ArrayList<Token> split(String expression) throws CantResolveTokenException, NoSuchOperatorException {

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
                        tokens.add(new Token(tokenStr, getTokenType(tokenStr)));
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
                        tokens.add(new Token(tokenStr, getTokenType(tokenStr)));
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
                        tokens.add(new Token(tokenStr, getTokenType(tokenStr)));
                        currentToken = new StringBuilder();
                        currentGroup = CharGroupMode.CONTINUE;
                    }
                    else {
                        CharGroupMode newGroup = getCharGroup(currentChar);
                        if (newGroup != currentGroup){
                            // после имени встретился оператор (или наоборот)
                            if (currentGroup != CharGroupMode.CONTINUE) {
                                String tokenStr = currentToken.toString();
                                tokens.add(new Token(tokenStr, getTokenType(tokenStr)));
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
        tokens.add(new Token(currentToken.toString(), getTokenType(currentToken.toString())));

        // убираем пустые токены
        tokens.removeIf(token -> token.Type == TokenType.EMPTY);

        splitOperatorBunches(tokens);

        for (Token token : tokens) System.out.println(token.Content + " " + token.Type);

        return tokens;
    }

    private void splitOperatorBunches(ArrayList<Token> tokens) throws NoSuchOperatorException {

        // тип оператора, который мы ищем
        TokenType operatorType = TokenType.PREFIX_UNARY_OPERATOR;

        for (int tokenIndex = 0; tokenIndex < tokens.size(); tokenIndex++) {
            Token token = tokens.get(tokenIndex);

            // если в токене нет операторов - просто пропускаем его
            // важно то, что нам не будут попадаться уже разделённые операторы
            if (token.Type != TokenType.OPERATOR_BUNCH) {
                operatorType = TokenType.POSTFIX_UNARY_OPERATOR;
                continue;
            }

            // откуда начинаем поиск нового оператора в том же токене
            // это проще, чем выполнять substring и заменять её
            int tokenStartIndex = 0;
            // сдвиг для добавляемых токенов
            int newTokenIndexOffset = 1;
            // надо ли продолжать обрабатывать токен
            boolean continueToken = true;

            while (continueToken) {

                StringBuilder currentOperator = new StringBuilder();
                // последний найденный оператор
                String lastFoundOperator = "";

                switch (operatorType) {

                    case POSTFIX_UNARY_OPERATOR -> {

                        // проверяем все подстроки, начинающиеся с конца предыдущего оператора и до каждого символа
                        for (int charIndex = tokenStartIndex; charIndex < token.Content.length(); charIndex++) {
                            currentOperator.append(token.Content.charAt(charIndex));

                            String currentOperatorStr = currentOperator.toString();
                            // если оператор существует - записываем его как последний найденный
                            if (getPostfixUnaryOperator(currentOperatorStr) != null) {
                                lastFoundOperator = currentOperatorStr;
                            }
                        }

                        // оператор не был найден
                        if (lastFoundOperator.isEmpty()) {
                            // если мы дошли до конца токена - удаляем его и идём к следующему
                            if (tokenStartIndex == token.Content.length()) {
                                continueToken = false;
                                tokens.remove(tokenIndex);
                                tokenIndex += newTokenIndexOffset - 2;
                            }
                            // дальше ищем бинарные
                            operatorType = TokenType.BINARY_OPERATOR;
                        } else {
                            // оператор был найден, сохраняем его
                            tokens.add(tokenIndex + newTokenIndexOffset, new Token(lastFoundOperator, TokenType.POSTFIX_UNARY_OPERATOR));
                            newTokenIndexOffset++;
                            tokenStartIndex += lastFoundOperator.length();
                        }
                    }
                    case BINARY_OPERATOR -> {
                        for (int charIndex = tokenStartIndex; charIndex < token.Content.length(); charIndex++) {
                            currentOperator.append(token.Content.charAt(charIndex));

                            String currentOperatorStr = currentOperator.toString();
                            if (getBinaryOperator(currentOperatorStr) != null) {
                                lastFoundOperator = currentOperatorStr;
                            }
                        }

                        // если мы ищем бинарный оператор, а токен не закончился - то он должен быть найден, иначе строка неверная
                        if (lastFoundOperator.isEmpty()) throw new NoSuchOperatorException(currentOperator.toString());
                        else {
                            // если был найден - добавляем его, и ищем префиксные унарные (два бинарных рядом никогда не стоят)
                            tokens.add(tokenIndex + newTokenIndexOffset, new Token(lastFoundOperator, TokenType.BINARY_OPERATOR));
                            newTokenIndexOffset++;
                            tokenStartIndex += lastFoundOperator.length();
                            operatorType = TokenType.PREFIX_UNARY_OPERATOR;
                            // если мы дошли до конца токена - удаляем его и идём к следующему
                            if (tokenStartIndex == token.Content.length()) {
                                continueToken = false;
                                tokens.remove(tokenIndex);
                                tokenIndex += newTokenIndexOffset - 2;
                            }
                        }
                    }
                    case PREFIX_UNARY_OPERATOR -> {

                        for (int charIndex = tokenStartIndex; charIndex < token.Content.length(); charIndex++) {
                            currentOperator.append(token.Content.charAt(charIndex));

                            String currentOperatorStr = currentOperator.toString();
                            if (getPrefixUnaryOperator(currentOperatorStr) != null) {
                                lastFoundOperator = currentOperatorStr;
                            }
                        }

                        if (lastFoundOperator.isEmpty()) {
                            // дошли до конца токена, но ещё остались символы, которые не похожи ни на один оператор
                            if (tokenStartIndex != token.Content.length()) {
                                throw new NoSuchOperatorException(currentOperator.toString(), true);
                            }
                            continueToken = false;
                            tokens.remove(tokenIndex);
                            tokenIndex += newTokenIndexOffset - 2;
                        } else {
                            tokens.add(tokenIndex + newTokenIndexOffset, new Token(lastFoundOperator, TokenType.PREFIX_UNARY_OPERATOR));
                            newTokenIndexOffset++;
                            tokenStartIndex += lastFoundOperator.length();
                        }
                    }

                }
            }
        }
    }

    private BinaryOperator getBinaryOperator(String literal) {
        return BinaryOperators.get(literal);
    }

    private UnaryOperator getPrefixUnaryOperator(String literal) {
        return PrefixUnaryOperators.get(literal);
    }

    private UnaryOperator getPostfixUnaryOperator(String literal) {
        return PostfixUnaryOperators.get(literal);
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
        TIME,

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

        FUNCTION,

        EMPTY,
    }

    private enum CharGroupMode {
        NAME, // в токене присутствуют цифры, буквы и нижнее подчеркивание
        OPERATOR, // противоположен NAME
        CONTINUE // продолжить этот в любом случае
    }

    public static boolean isBoolean(String token) {
        token = token.toLowerCase(Locale.ROOT);
        return token.equals("true") || token.equals("false");
    }

    public static TokenType getTokenType(String token) throws CantResolveTokenException {

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

        throw new CantResolveTokenException(token);
    }

    private static CharGroupMode getCharGroup(char Char) {
        if (Char == '_' || Character.isLetterOrDigit(Char)) return CharGroupMode.NAME;
        else return CharGroupMode.OPERATOR;
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

package com.scalpelred.whilechat.compiler;

import com.scalpelred.whilechat.compiler.Parser.TokenType;

public class Token {

    public final TokenType Type;
    public final String Content;

    public Token(String content, TokenType type) {
        Type = type;
        Content = content;
    }


}
